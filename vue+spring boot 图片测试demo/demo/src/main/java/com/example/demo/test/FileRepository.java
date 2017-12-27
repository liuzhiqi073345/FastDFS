package com.example.demo.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface FileRepository extends JpaRepository<FastDfs, String>{
	
}
