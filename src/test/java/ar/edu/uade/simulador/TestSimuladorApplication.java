package ar.edu.uade.simulador;

import org.springframework.boot.SpringApplication;

public class TestSimuladorApplication {

	public static void main(String[] args) {
		SpringApplication.from(SimuladorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
