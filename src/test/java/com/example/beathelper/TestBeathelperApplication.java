package com.example.beathelper;

import org.springframework.boot.SpringApplication;

public class TestBeathelperApplication {

	public static void main(String[] args) {
		SpringApplication.from(BeathelperApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
