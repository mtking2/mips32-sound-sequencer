.data

pitchArray:		 .word  64,  66, 67,  69,  71,   73,  74,  76	# The eight pitches for the 8 notes (-1 = rest)
durationArray:	 .word 500  200, 200, 200, 200, 200, 200, 500 		# Keep duration for each note
volumeArray:	 .word 127, 127, 127, 127, 127, 127, 127, 127	# Volume for each note


.text


setup:
	la $t0, pitchArray	# Use $t0 to store current pitch address
	la $t1, durationArray	# Use $t1 to store current duration
	la $t2, volumeArray	# Use $t2 to store volume
	
	li $t3, 0	# Used to count how many times loop has finished
	li $t4, 8	# How many times the loop will run
	li $t5, 0   # How many notes have been played
	
main:
	jal playSequence

playSequence:
	li $v0, 37 	# Load service
	li $a2, 0   # Load instrument
	
	lw $a0, 0($t0)		# Load current pitch into param
	addi $t0, $t0, 4	# Increment pitch address by 4

	li $a1, 300
	
	lw $a3, 0($t2)		# Load current volume into param
	addi $t2, $t2, 4	# Increment volume address by 4

	syscall		# Play sound

	noteSleep:
		li $v0, 32
		lw $a0, 0($t1)
		addi $t1, $t1, 4	# Increment duration address by 4
		syscall

	addi $t5, $t5, 1	# Increment times looped by 1
	
	bne $t5, $t4, main

reset:
	
	la $t0, pitchArray	# Use $t0 to store current pitch address
	la $t1, durationArray	# Use $t1 to store current duration
	la $t2, volumeArray	# Use $t2 to store volume

Loop:
	addi $t3, $t3, 1	# Increment times looped by 1
	li $t5, 0   # How many notes have been played
	beq $t3, $t4, exit
	j main

exit:
	li $v0, 10	# Load exit system call
	syscall
	
