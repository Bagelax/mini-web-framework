package test;

import annotations.Autowired;
import annotations.Controller;
import annotations.Qualifier;

@Controller
public class SampleControllerTwo {
    @Autowired
    @Qualifier("two")
    public InterfaceTwo i;

    @Autowired
    public ClsOne clsOne;
}
