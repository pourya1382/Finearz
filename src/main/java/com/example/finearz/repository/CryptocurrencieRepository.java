package com.example.finearz.repository;

import com.example.finearz.model.Cryptocurrencie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptocurrencieRepository extends JpaRepository<Cryptocurrencie,Long> {

}
