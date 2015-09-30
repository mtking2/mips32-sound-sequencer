.data

instruments:	.word 1, 19, 34, 42

# TRACK 1 #
pitchArray1:		 .word  64,  66, 67,  69,  71,   73,  74,  76	# The eight pitches for the 8 notes (-1 = rest)
durationArray1:	 .word 200  200, 200, 200, 200, 200, 200, 200 		# Keep duration for each note
volumeArray1:	 .word 127, 127, 127, 127, 127, 127, 127, 127	# Volume for each note

# TRACK 2 #

pitchArray2:		 .word  64,  62, 60,  59,  57,   55,  54,  52	# The eight pitches for the 8 notes (-1 = rest)
durationArray2:	 .word 200  200, 200, 200, 200, 200, 200, 200 		# Keep duration for each note
volumeArray2:	 .word 127, 127, 127, 127, 127, 127, 127, 127	# Volume for each note


.text


setup:
	la $t0, pitchArray1	# Use $t0 to store current pitch address
	la $t1, durationArray1	# Use $t1 to store current duration
	la $t2, volumeArray1	# Use $t2 to store volume

	la $t7, pitchArray2	# Use $t7 to store current pitch address		# HORRIBLE CODE CHANGE LATER
	la $t8, durationArray2	# Use $t8 to store current duration
	la $t9, volumeArray2	# Use $t9 to store volume
	
	li $t3, 0	# Used to count how many times loop has finished
	li $t4, 8	# How many times the loop will run
	li $t5, 0      	# How many notes have been played
	li $t6, 8	# How many notes to play
	
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

	li $a2, 22   # Load instrument
	
	lw $a0, 0($t7)		# Load current pitch into param
	addi $t7, $t7, 4	# Increment pitch address by 4

	li $a1, 300
	
	lw $a3, 0($t9)		# Load current volume into param
	addi $t9, $t9, 4	# Increment volume address by 4

	syscall		# Play sound

	noteSleep:
		li $v0, 32
		lw $a0, 0($t8)
		addi $t8, $t8, 4	# Increment duration address by 4
		syscall

	addi $t5, $t5, 1	# Increment times looped by 1
	
	bne $t5, $t6, main	# If amount of notes to play is not reached, play next note

reset:
	
	la $t0, pitchArray1	# Use $t0 to store current pitch address
	la $t1, durationArray1	# Use $t1 to store current duration
	la $t2, volumeArray1	# Use $t2 to store volume

	la $t7, pitchArray2	# Use $t7 to store current pitch address
	la $t8, durationArray2	# Use $t8 to store current duration
	la $t9, volumeArray2	# Use $t9 to store volume

Loop:
	addi $t3, $t3, 1	# Increment times looped by 1
	li $t5, 0   # How many notes have been played
	beq $t3, $t4, exit
	j main

exit:
	li $v0, 10	# Load exit system call
	syscall
	
