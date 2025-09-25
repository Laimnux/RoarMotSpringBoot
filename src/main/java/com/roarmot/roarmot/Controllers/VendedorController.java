package com.roarmot.roarmot.Controllers; // Asegúrate de que el paquete sea el correcto

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;

@Controller
@RequestMapping("/panel")
public class VendedorController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/products/";

    /**
     * Maneja la solicitud GET para mostrar el panel de inicio del vendedor.
     * Esta es la vista principal a la que el usuario accederá desde el enlace.
     */
    @GetMapping
    public String showPanelInicio(Model model) {
        model.addAttribute("title", "Dashboard - ROARMOT");
        model.addAttribute("activePage", "inicio");
        model.addAttribute("nombreUsuario", "Proveedor ROARMOT"); 
        model.addAttribute("contentFragment", "fragmentos/panelVendedor/panelInicio");

        return "PanelVendedor";
    }

    @GetMapping("/addProduct")
    public String showAddProductForm(Model model) {
        // Datos comunes para el layout
        model.addAttribute("title", "Agregar Producto - ROARMOT");
        model.addAttribute("activePage", "addProduct");
        model.addAttribute("nombreUsuario", "Proveedor ROARMOT"); 

        // Indica al layout qué fragmento inyectar en <main>
        model.addAttribute("contentFragment", "fragmentos/panelVendedor/addProduct");

        // Retorna el layout principal
        return "PanelVendedor";
    }

    @PostMapping("/ProductProcess")
    public String processAddProductForm(
            @RequestParam("tipo") String tipo,
            @RequestParam("nombre") String nombre,
            @RequestParam("marca") String marca,
            @RequestParam("Talla") String talla,
            @RequestParam("cantidad") int cantidad,
            @RequestParam(value = "lote", required = false) String lote,
            @RequestParam(value = "precio_venta") double precioVenta,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam("subcategoria") String subcategoria,
            @RequestParam("imagen") List<MultipartFile> imagenes, 
            RedirectAttributes redirectAttributes) {
        
        if (nombre.isEmpty() || marca.isEmpty() || categoria.isEmpty() || imagenes.isEmpty() || imagenes.get(0).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Todos los campos marcados con * y la imagen principal son obligatorios.");
            return "redirect:/panel/addProduct";
        }
        
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); 
            }

            for (MultipartFile file : imagenes) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);
                System.out.println("Archivo de imagen subido y listo para DB: " + fileName);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "✅ Producto agregado y sus imágenes subidas con éxito!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Error de servidor al subir las imágenes. Intente de nuevo.");
            return "redirect:/panel/addProduct";
        } catch (Exception e) {
             e.printStackTrace();
             redirectAttributes.addFlashAttribute("errorMessage", "❌ Ocurrió un error inesperado al procesar el producto.");
             return "redirect:/panel/addProduct";
        }

        return "redirect:/panel/addProduct";
    }

    @GetMapping("/editProduct")
    public String mostrarLogin() {
        return "fragmentos/panelVendedor/editProducto";
    }
}