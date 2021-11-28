package com.webscolaire.registry.mapper;

import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.dto.RegisterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterMapper extends EntityMapper<RegisterDto, Register> {}
