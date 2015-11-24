.text

setup:
	li $t5, 0      	# Which note is being played
	
	la $t6, beats	# Load address for notes to play
	lw $t6, 0($t6)	# Load how many notes to play

	lw $t7, tempo	# Load tempo
	li $t8, 0	# Offset for array values

main:
	# Load first track note data
	jal loadFirstTrack
	# Play note from first track
	jal playNote
	
	# Load second note data
	jal loadSecondTrack
	# Play note from second track
	jal playNote
	
	# Load third track data
	jal loadThirdTrack
	# Play note from third track
	jal playNote
	
	# Load fourth track data
	jal loadFourthTrack
	# Play note from fourth track
	jal playNote
	
	# Sleep until next beat
	jal sleep

	# Increment notes played by 1
	addi $t5, $t5, 1
	# Increment offset by 1 byte
	addi $t8, $t8, 4

	# if notes played == number of beats, go back to beginning of sequence
	beq $t5, $t6, reset
	
	# Loop and play next note
	j main
	
# Subroutines #

playNote:

	li $v0, 37 	# Load MIDI playing service

	lw $a0, 0($t0)		# Load current note's pitch

	# If pitch is -1, skip playing (note is rest)
	beq $a0, -1, jump

	lw $a1, 0($t1)		# Load current note's duration
	
	lw $a3, 0($t2)		# Load current note's volume

	syscall		# Play sound

jump:

	jr $ra		# Jump to return address
	
reset:
	
	li $t5, 0  	# How many notes have been played
	li $t8, 0	# Reset offset to 0

	# Reset to first track data
	jal loadFirstTrack

	j main

sleep:
	# Sleep until next beat
	li $v0, 32		# Load sleep service
	add $a0, $0, $t7	# Time to sleep == tempo
	syscall

	jr $ra

loadFirstTrack:
	la $t0, pitchArray1		# $t0 = first track's pitch data
	la $t1, durationArray1	# $t1 = first track's duration data
	la $t2, volumeArray1	# $t2 = first track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	li $v0, 38 	# Load MIDI channel service

	li $a0, 0	# Load channel number

	la $a1, instruments	# Load ADDRESS of instrument array into param
	lb $a1, 0($a1)		# Load second instrument from address (offset for instrument 1 == 0 bytes)

	syscall

	li $a2, 0	# Set channel number to first channel

	jr $ra			# Jump to return address

loadSecondTrack:
	la $t0, pitchArray2	# $t0 = second track's pitch data
	la $t1, durationArray2	# $t1 = second track's duration data
	la $t2, volumeArray2	# $t2 = second track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	li $v0, 38 	# Load MIDI channel service

	li $a0, 1	# Load channel number

	la $a1, instruments	# Load ADDRESS of instrument array into param
	lb $a1, 1($a1)		# Load second instrument from address

	syscall

	li $a2, 1	# Set channel to second channel

	jr $ra			# Jump to return address

loadThirdTrack:
	la $t0, pitchArray3	# $t0 = third track's pitch data
	la $t1, durationArray3	# $t1 = third track's duration data
	la $t2, volumeArray3	# $t2 = third track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	li $v0, 38 	# Load MIDI channel service

	li $a0, 2	# Load channel number

	la $a1, instruments	# Load ADDRESS of instrument array into param
	lb $a1, 2($a1)		# Load third instrument from address

	syscall

	li $a2, 2	# Set channel to third channel

	jr $ra			# Jump to return address

loadFourthTrack:
	la $t0, pitchArray4	# $t0 = fourth track's pitch data
	la $t1, durationArray4	# $t1 = fourth track's duration data
	la $t2, volumeArray4	# $t2 = fourth track's volume data

	# Add offset to array addresses
	add $t0, $t0, $t8
	add $t1, $t1, $t8
	add $t2, $t2, $t8

	li $v0, 38 	# Load MIDI channel service

	li $a0, 3	# Load channel number

	la $a1, instruments	# Load ADDRESS of instrument array into param
	lb $a1, 3($a1)		# Load fourth instrument from address

	syscall

	li $a2, 3	# Set channel to fourth channel

	jr $ra			# Jump to return address