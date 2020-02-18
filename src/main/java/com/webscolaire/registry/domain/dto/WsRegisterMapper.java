package com.webscolaire.registry.domain.dto;

import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class WsRegisterMapper implements Function<WsRegisterDto, WsRegister> {

    @Override
    public WsRegister apply(WsRegisterDto registerDto) {
        return new WsRegister()
                .setWebscolaireType(WebscolaireType.valueOf(registerDto.getWebscolaireType()))
                .setDateOfBirth(registerDto.getDateOfBirth())
                .setLastname(registerDto.getLastname().toUpperCase())
                .setSex(SexType.valueOf(registerDto.getSex()))
                .setFirstname(registerDto.getFirstname().toUpperCase());
    }
}
