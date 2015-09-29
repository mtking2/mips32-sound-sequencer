.data

pitchArray:		.word  64,  66, 67,  69,  71,   73,  74,  76	# The eight pitches for the 8 notes (-1 = rest)
durationArray:	.word 150, 150, 150, 150, 150, 150, 150, 150	# Keep duration for each note
volumeArray:	.word 127, 127, 127, 127, 127, 127, 127, 127	# Volume for each note

.text

	li $v0, 33 	# Load service
	
	la $t0, pitchArray	# Use $t0 to store current pitch address
	la $t1, durationArray	# Use $t1 to store current duration
	la $t2, volumeArray	# Use $t2 to store volume
	
	li $t3, 0	# Used to count how many times loop has finished
	li $t4, 8	# How many times the loop will run
	
	li $a2, 0	# Load instrument
	
main:
	
	lw $a0, 0($t0)		# Load current pitch into param
	addi $t0, $t0, 4	# Increment pitch address by 4
	lw $a1 0($t1)		# Load current pitch into param
	addi $t1, $t1, 4	# Increment pitch address by 4
	lw $a3, 0($t2)		# Load current pitch into param
	addi $t2, $t2, 4	# Increment pitch address by 4
	
	syscall		# Play sound
	
	addi $t3, $t3, 1	# Increment times looped by 1
	
	bne $t3, $t4, loop
	
reset:

	la $t0, pitchArray	# Use $t0 to store current pitch address
	la $t1, durationArray	# Use $t1 to store current duration
	la $t2, volumeArray	# Use $t2 to store volume
	
loop:
	
	j main
	
exit:

	li $v0, 10	# Load exit system call
	syscall
	
