
.text

main:
	
	la $v0, 33 	# Load service
	
	li $a0, 64 	# Pitch
	li $a1, 150	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
	li $a1, 150	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
	li $a1, 150	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
	li $a0, 60 	# Pitch
	li $a1, 150	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
	li $a0, 64 	# Pitch
	li $a1, 250	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
	li $a0, 67 	# Pitch
	li $a1, 800	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
	li $a0, 55 	# Pitch
	li $a1, 600	# Duration
	li $a2, 114	# Instrument
	li $a3, 127 # Volume
	syscall
	
exit:

	li $v0, 10	# Terminate excution
	syscall
	