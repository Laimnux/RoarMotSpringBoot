package com.roarmot.roarmot.Services;

import com.roarmot.roarmot.dto.MotoDTO;
import com.roarmot.roarmot.models.Moto;
import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.MotoRepository;
import com.roarmot.roarmot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MotoService {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nueva moto para un usuario
    public MotoDTO crearMoto(MotoDTO motoDTO, Long usuarioId) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // Validar que no exista otra moto con la misma placa
        if (motoRepository.existsByPlacaMoto(motoDTO.getPlacaMoto())) {
            throw new RuntimeException("Ya existe una moto con la placa: " + motoDTO.getPlacaMoto());
        }

        // Validar kilometraje
        if (motoDTO.getKilometraje() < 0) {
            throw new RuntimeException("El kilometraje no puede ser negativo");
        }

        // Convertir DTO a Entity
        Moto moto = convertirDtoAEntity(motoDTO);
        moto.setUsuario(usuario);

        // Guardar en la base de datos
        Moto motoGuardada = motoRepository.save(moto);

        // Convertir Entity a DTO para respuesta
        return convertirEntityADto(motoGuardada);
    }

    // Obtener todas las motos de un usuario
    public List<MotoDTO> obtenerMotosPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        List<Moto> motos = motoRepository.findByUsuario(usuario);
        
        return motos.stream()
                .map(this::convertirEntityADto)
                .collect(Collectors.toList());
    }

    // Obtener una moto por su ID
    public MotoDTO obtenerMotoPorId(Long motoId) {
        Moto moto = motoRepository.findById(motoId)
                .orElseThrow(() -> new RuntimeException("Moto no encontrada con ID: " + motoId));
        
        return convertirEntityADto(moto);
    }

    // Actualizar una moto
    public MotoDTO actualizarMoto(Long motoId, MotoDTO motoDTO) {
        Moto motoExistente = motoRepository.findById(motoId)
                .orElseThrow(() -> new RuntimeException("Moto no encontrada con ID: " + motoId));

        // Validar que la placa no esté en uso por otra moto
        if (!motoExistente.getPlacaMoto().equals(motoDTO.getPlacaMoto()) &&
            motoRepository.existsByPlacaMoto(motoDTO.getPlacaMoto())) {
            throw new RuntimeException("Ya existe otra moto con la placa: " + motoDTO.getPlacaMoto());
        }

        // Actualizar campos
        motoExistente.setMarcaMoto(motoDTO.getMarcaMoto());
        motoExistente.setModeloMoto(motoDTO.getModeloMoto());
        motoExistente.setColorMoto(motoDTO.getColorMoto());
        motoExistente.setImagenMoto(motoDTO.getImagenMoto());
        motoExistente.setSoatMoto(motoDTO.getSoatMoto());
        motoExistente.setTecnomecanica(motoDTO.getTecnomecanica());
        motoExistente.setPlacaMoto(motoDTO.getPlacaMoto());
        motoExistente.setKilometraje(motoDTO.getKilometraje());

        Moto motoActualizada = motoRepository.save(motoExistente);
        return convertirEntityADto(motoActualizada);
    }

    // Eliminar una moto
    public void eliminarMoto(Long motoId) {
        if (!motoRepository.existsById(motoId)) {
            throw new RuntimeException("Moto no encontrada con ID: " + motoId);
        }
        motoRepository.deleteById(motoId);
    }

    // Métodos de conversión DTO ↔ Entity

    private Moto convertirDtoAEntity(MotoDTO dto) {
        Moto moto = new Moto();
        moto.setMarcaMoto(dto.getMarcaMoto());
        moto.setModeloMoto(dto.getModeloMoto());
        moto.setColorMoto(dto.getColorMoto());
        moto.setImagenMoto(dto.getImagenMoto());
        moto.setSoatMoto(dto.getSoatMoto());
        moto.setTecnomecanica(dto.getTecnomecanica());
        moto.setPlacaMoto(dto.getPlacaMoto());
        moto.setKilometraje(dto.getKilometraje());
        return moto;
    }

    private MotoDTO convertirEntityADto(Moto moto) {
        MotoDTO dto = new MotoDTO();
        dto.setMarcaMoto(moto.getMarcaMoto());
        dto.setModeloMoto(moto.getModeloMoto());
        dto.setColorMoto(moto.getColorMoto());
        dto.setImagenMoto(moto.getImagenMoto());
        dto.setSoatMoto(moto.getSoatMoto());
        dto.setTecnomecanica(moto.getTecnomecanica());
        dto.setPlacaMoto(moto.getPlacaMoto());
        dto.setKilometraje(moto.getKilometraje());
        return dto;
    }
}