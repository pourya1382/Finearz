package com.example.finearz.service;

import com.example.finearz.repository.CryptocurrencieRepository;
import org.springframework.stereotype.Service;

@Service
public class CryptocurrencieService {
private CryptocurrencieRepository Repository;

    public CryptocurrencieService(CryptocurrencieRepository repository) {
        Repository = repository;
    }


}
