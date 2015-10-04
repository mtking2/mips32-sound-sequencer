.data

instruments:	.word 1, 1, 1, 1

# TRACK 1 DATA #

pitchArray1:	.word  64,  66,  67,  69,  71,  73,  74,  76	# The pitch for each note
durationArray1:	.word 200  200, 200, 200, 200, 200, 200, 200 	# The duration of each note
volumeArray1:	.word 127, 127, 127, 127, 127, 127, 127, 127	# The volume of each note

# TRACK 2 DATA #

pitchArray2:	.word  50,  1,  71,  73,  74,  76,  78,  79	# The pitch for each note
durationArray2: .word 200  200, 200, 200, 200, 200, 200, 200 	# The duration of each note
volumeArray2:	.word 127, 127, 127, 127, 127, 127, 127, 127	# The volume of each note

# TRACK 3 DATA #

pitchArray3:	.word  71,  73,  74,  76,  78,  79,  81,  83	# The pitch for each note
durationArray3:	.word 200  200, 200, 200, 200, 200, 200, 200 	# The duration of each note
volumeArray3:	.word 127, 127, 127, 127, 127, 127, 127, 127	# The volume of each note

# TRACK 4 DATA #

pitchArray4:	.word  76,  78,  79,  81,  83,  85,  86,  88	# The pitch for each note
durationArray4: .word 200  200, 200, 200, 200, 200, 200, 200 	# The duration of each note
volumeArray4:	.word 127, 127, 127, 127, 127, 127, 127, 127	# The volume of each note


.text


setup:
	li $t3, 0	# How many times loop has finished
	li $t4, 8	# How many times the loop will run
	li $t5, 0      	# Which note is being played
	li $t6, 8	# How many notes to play

	li $t7, 400	# Load tempo
	li $t8, 0	# Offset for array values

main:
	jal loadFirstTrack
	jal playNote
	jal loadSecondTrack
	jal playNote
	jal loadThirdTrack
	jal playNote
	jal loadFourthTrack
	jal playNote
	jal sleep

	addi $t5, $t5, 1	# Increment notes played by 1
	addi $t8, $t8, 4	# Increment offset by 1 byte

	beq $t5, $t6, reset
	j main


playNote:

	li $v0, 37 	# Load MIDI playing service

	lw $a0, 0($t0)		# Load current pitch

	lw $a1, 0($t1)		# Load current duration
	
	lw $a3, 0($t2)		# Load current volume

	syscall		# Play sound

	jr $ra		# Jump to return address
	
reset:

	addi $t3, $t3, 1	# Increment times looped by 1
	
	li $t5, 0  	# How many notes have been played
	li $t8, 0	# Reset offset to 0

	# Reset to first track data
	jal loadFirstTrack

	beq $t3, $t4, exit
	j main

sleep:
	# Sleep until next beat
	li $v0, 32		# Load sleep service
	add $a0, $0, $t7	# Time to sleep == tempo
	syscall

	jr $ra

loadFirstTrack:
	la $t0, pitchArray1	# $t0 = first track's pitch data
	la $t1, durationArray1	# $t1 = first track's duration data
	la $t2, volumeArray1	# $t3 = first track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	la $a2, instruments	# Load ADDRESS of instrument array into param
	lw $a2, 0($a2)		# Load second instrument from address (offset for instrument 1 == 0 bytes)

	jr $ra			# Jump to return address

loadSecondTrack:
	la $t0, pitchArray2	# $t0 = second track's pitch data
	la $t1, durationArray2	# $t1 = second track's duration data
	la $t2, volumeArray2	# $t3 = second track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	la $a2, instruments	# Load ADDRESS of instrument array into param
	addi $a2, $a2, 4	# Add 4 to address to point to second instrument
	lw $a2, 0($a2)		# Load second instrument from address

	jr $ra			# Jump to return address

loadThirdTrack:
	la $t0, pitchArray3	# $t0 = third track's pitch data
	la $t1, durationArray3	# $t1 = third track's duration data
	la $t2, volumeArray3	# $t3 = third track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	la $a2, instruments	# Load ADDRESS of instrument array into param
	addi $a2, $a2, 8	# Add 8 to address to point to third instrument
	lw $a2, 0($a2)		# Load third instrument from address

	jr $ra			# Jump to return address

loadFourthTrack:
	la $t0, pitchArray4	# $t0 = fourth track's pitch data
	la $t1, durationArray4	# $t1 = fourth track's duration data
	la $t2, volumeArray4	# $t3 = fourth track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	la $a2, instruments	# Load ADDRESS of instrument array into param
	addi $a2, $a2, 12	# Add 12 to address to point to fourth instrument
	lw $a2, 0($a2)		# Load fourth instrument from address

	jr $ra			# Jump to return address

exit:
	li $v0, 10	# Load exit system call
	syscall
