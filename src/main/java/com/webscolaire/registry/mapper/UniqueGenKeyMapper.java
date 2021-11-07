package com.webscolaire.registry.mapper;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.dto.UniqueGenKeyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UniqueGenKeyMapper extends EntityMapper<UniqueGenKeyDto, UniqueGenKey> {}
