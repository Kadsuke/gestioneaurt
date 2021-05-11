package com.onea.sidot.passerelle.cucumber;

import com.onea.sidot.passerelle.PasserelleApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = PasserelleApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
