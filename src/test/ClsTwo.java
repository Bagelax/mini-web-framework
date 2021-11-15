package test;

import annotations.Autowired;
import annotations.Component;
import annotations.Qualifier;

@Qualifier("two")
@Component
public class ClsTwo implements InterfaceTwo {
    @Autowired
    private ClsThree three;
}
