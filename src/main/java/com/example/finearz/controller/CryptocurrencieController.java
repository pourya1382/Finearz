package com.example.finearz.controller;

import com.example.finearz.service.CryptocurrencieService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finearz")
public class CryptocurrencieController {
private CryptocurrencieService service;

    public CryptocurrencieController(CryptocurrencieService service) {
        this.service = service;
    }



}
