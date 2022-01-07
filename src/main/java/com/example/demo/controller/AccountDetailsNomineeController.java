package com.example.demo.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.connector.AccountServiceConnector;
import com.example.demo.vo.RespDetails;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@RestController
public class AccountDetailsNomineeController {

	@Autowired
	AccountServiceConnector accountServiceConnector;

	@GetMapping("/accountservice/{crnNo}")
	public @ResponseBody RespDetails findAccountDetailsNominee(@PathVariable String crnNo) {
		
		 RespDetails rd = accountServiceConnector.send(crnNo);
		 System.out.println(new Gson().toJson(rd));
		 System.out.println(rd.data.acct_list.get(0).account_id);
		 return rd;
	}

}


