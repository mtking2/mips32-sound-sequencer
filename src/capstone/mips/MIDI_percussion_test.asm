

.text

li $v0, 38	# MIDI change channel service
li, $a0, 9	# MIDI Channel 10 (9) is for GM1 Percussion Set
li $a1, 50	# Key number to select instrument
syscall



li $v0, 37	# MIDI output service
li $a0, 50	# First parameter normally sets the pitch but, in the case of of MIDI channel 10,
				#  the pitch actually selects percussion instrument.
li $a1, 500 # duration parameter
li $a2, 9	# Normally the instrument parameter. Service 37 uses this parameter to select the MIDI channel to ouput.
li $a3, 127	# volume parameter
syscall


## Sleep until next beat ##
li $v0, 32		# $v0 = syscall 32 (sleep)
		
# Load time to wait
li $a0, 500	# $a0 = &timeToWait
syscall
