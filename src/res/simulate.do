set top_level work.%TOP_LEVEL_ENTITY%

vsim -novopt $top_level

vcd add -r -file simulated.vcd *
run %SIMULATION_TIME%
vcd flush simulated.vcd

quit -force