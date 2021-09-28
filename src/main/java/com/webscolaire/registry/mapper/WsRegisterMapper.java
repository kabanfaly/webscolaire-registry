package com.webscolaire.registry.mapper;

import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Function;

@Component
public class WsRegisterMapper implements Function<WsRegisterDto, WsRegister> {

    @Override
    public WsRegister apply(WsRegisterDto registerDto) {
        return new WsRegister()
            .setSourceId(registerDto.getSourceId())
            .setWebscolaireType(registerDto.getWebscolaireType())
            .setDateOfBirth(registerDto.getDateOfBirth())
            .setLastname(registerDto.getLastname().toUpperCase(Locale.FRANCE))
            .setFirstname(registerDto.getFirstname().toUpperCase(Locale.FRANCE))
            .setSex(registerDto.getSex())
            .setGeneratedNumber(registerDto.getGeneratedNumber());
    }
}
