package test;

import annotations.Autowired;
import annotations.Bean;
import annotations.Qualifier;

@Bean(scope = "singleton")
public class ClsThree {
    @Autowired
    @Qualifier("one")
    private InterfaceOne i;
}
