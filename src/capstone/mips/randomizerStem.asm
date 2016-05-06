output:		.space	1
beats:		.word	16
scale:		.byte	2, 2, 1, 2, 2, 2, 1

.text

#############
# REGISTERS #
#############
#
# $s0 - size of line data
# $s1 - file descriptor
# $s2 - track counter / instrument counter
# $s3 - beat counter
# $s4 - value type counter
# $s5 - root note
# $s6 - mode (used temporarily)
#
### MODES ###
#
# 0 - major
# 1 - dorian
# 2 - phrygian
# 3 - lydian
# 4 - mixolydian
# 5 - minor (locrian)
# 6 - locrian

main:
	# Open file for writing
	li $v0, 13		# $v0 = 13 (open file)
	la $a0, filename	# $a0 = name of file
	li $a1, 1		# $a1 = 1 (open for write/create)
	li $a2, 0		# $a2 = 0 (ignored)
	syscall

	move $s1, $v0	# $s1 = file descriptor

	# Generate tempo between 80 (slow) to 240 (fast)
	li $v0, 42	# $v0 = 42 (random int in range)
	li $a0, 0	# $a0 = 0 (randomizer id)
	li $a1, 161	# $a1 = 161 (exclusive upper bound)
	syscall

	addi $a0, $a0, 80	# $a0 = $a0 (result) + 80

	jal toString
	jal writeToOutput	# Write to file

	jal writeNL	# Write newline

	# Generate instruments
	li $s2, 0	# $t1 = 0 (counter)

	# First track is lead melody, second track is bass,
	# third & fourth tracks are percussion

	# Generate track one instrument
	# - between 0 - 32, 41 - 96

	# Generate number between 0 and 88, if over 32, add 8 (skip bass instruments)
	li $v0, 42	# $v0 = 42 (random int in range)
	li $a0, 0	# $a0 = 0 (randomizer id)
	li $a1, 89	# $a1 = 89 (exclusive upper bound)
	syscall

	move $t0, $a0	# $t0 = $a0

	li $t1, 32			# $t0 = 32
	ble $t0, $t1, noAddEight	# If result <= 32, don't add eight

	addi $t0, $t0, 8	# $t0 = $t0 + 8

	noAddEight:
	move $a0, $t0	# $a0 = $t0
	jal toString
	jal writeToOutput

	# Generate track 2 instrument
	# - between 33 - 40 (we can change which instruments later if needed)

	li $v0, 42	# $v0 = 42 (random int in range)
	li $a0, 0	# $a0 = 0 (randomizer id)
	li $a1, 8	# $a1 = 8 (exclusive upper bound)
	syscall

	addi $a0, $a0, 33	# $a0 = $a0 + 33 (move to bass instrument range)

	jal toString
	jal writeToOutput

	# Instrument for percussion is ignored, use 1000
	# to designate to MIPS this will be a percussion track

	li $a0, 1000	# $a0 = 1000 (percussion designation)
	jal toString
	jal writeToOutput

	li $a0, 1000	# $a0 = 1000 (percussion designation)
	jal toString
	jal writeToOutput

	# Write newline to file
	jal writeNL

	# Generate mode
	li $v0, 42	# $v0 = 42 (random int in range)
	li $a0, 0	# $a0 = 0 (randomizer id)
	li $a1, 7	# $a1 = 7 (exclusive upper bound)
	syscall

	move $s6, $a0	# $s6 = $a0 (generated mode id)

	# Rewrite scale intervals based on mode

	# If major key then we don't have to do this, skip to end of loop
	beq $s6, $0, loopCheck

	# Save copy of scale onto stack
	subi $sp, $sp, 7	# $sp = $sp - 7	(7 bytes/notes)

	li $t7, 7	# $t7 = 7
	la $t2, scale	# $t2 = &scale
	move $t6, $0	# $t6 = 0 (counter)

	stackWriteLoop:
		move $t5, $t2		# $t5 = &scale
		add $t5, $t5, $t6	# $t5 = $t5 + $t6 (counter)
		lb $t5, 0($t5)		# $t5 = current scale interval

		move $t1, $sp		# $t1 = $sp
		add $t1, $t1, $t6	# $t1 = $t1 + $t6 (counter)

		sb $t5, 0($t1)		# Write $t5 onto address on stack at $t1

		addi $t6, $t6, 1	# Increment counter
		bne $t6, $t7, stackWriteLoop

	# Rewrite scale using stack copy
	move $t0, $s6	# $t0 = $s6 (counter for stack copy starts at mode id)
	move $t6, $0	# $t6 = 0 (second counter for scale in data section starts at 0)

	scaleLoop:
		move $t1, $sp		# $t1 = $sp
		add $t1, $t1, $t0	# $t1 = $t1 + $t0 (move to address of next interval in mode)

		lb $t3, 0($t1)	# $t3 = next interval

		move $t4, $t2		# $t4 = $t2 (&scale)
		add $t4, $t4, $t6	# $t4 = $t4 + $t0 (go to current interval in scale)

		sb $t3, 0($t4)	# Store interval into scale in data section

		addi $t0, $t0, 1	# Increment counter
		addi $t6, $t6, 1	# Increment second counter
		bne $t0, $t7, loopCheck	# If counter is 7 reset to 0, otherwise check if looping again
		move $t0, $0		# $t0 = 0

		loopCheck:
		bne $t0, $s6, scaleLoop	# Loop again until back to mode id

	# Return space on stack
	addi $sp, $sp, 7	# $sp = $sp + 7

	# Generate root note
	li $v0, 42	# $v0 = 42 (random int in range)
	li $a0, 0	# $a0 = 0 (randomizer id)
	li $a1, 13	# $a1 = 13 (exclusive upper bound)
	syscall

	move $s5, $a0		# $s5 = $a0 (result)
	addi $s5, $s5, 48	# $s5 = $s5 + 48 (make root between 48 and 60)

	# Generate each line of data
	# Save in file as pitch -> duration -> volume
	li $s2, 0	# $s2 = 0 (track counter)
	li $s4, 0	# Value type counter (pitch/volume/duration)

	trackLoop:
		li $s3, 0	# $s3 = 0 (beat counter)

		lineLoop:
			li $v0, 42	# $v0 = 42 (random int)
			move $a0, $s4	# $a0 = $t4 (use same randomizer as value type counter)

			beq $s4, $0, generatePitch	# If generating pitch, jump to section where we generate pitch

			li $t0, 1	# $t0 = 1
			beq $t0, $s4, durRange	# Process duration differently

			li $a0, 127	# $a1 = 128 (exclusive upper bound)
			j save

			durRange:
			li $a0, 1000	# $a1 = 1000 (exclusive upper bound)

			j save

			generatePitch:
			# 15% chance note is rest
			li $v0, 42	# $v0 = 42 (random int in range)
			li $a0, 0	# $a0 = 0 (randomizer id)
			li $a1, 15	# $a1 = 15 (exclusive upper bound)
			syscall

			bne $a0, $0, generateRegularPitch

			li $a0, 1000
			j save

			generateRegularPitch:
			# Generate scale degree
			li $v0, 42	# $v0 = 42 (random int in range)
			li $a0, 0	# $a0 = 0 (randomizer id)
			li $a1, 8	# $a1 = 8 (exclusive upper bound)
			syscall

			# $a0 is already generated scale degree, send to subroutine
			jal addToRoot

			save:
			# Save value to file
			# $a0 already contains value to convert into chars
			jal toString

			# Params are already set for writeToOutput
			jal writeToOutput

			# Increment beat
			addi $s3, $s3, 1	# $t3 = $t3 + 1

			la $t2, beats	# $t2 = &beats
			lw $t2, 0($t2)	# $t2 = beats

			# If all beats not processed, loop again
			bne $s3, $t2, lineLoop

			# Write newline
			jal writeNL

			# Increment value type
			addi $s4, $s4, 1	# $s4 = $s4 + 1
			li $s3, 0	# Reset beat counter

			li $t5, 3	# $t5 = 3 (number of types of values)

			# If all types of values not processed, loop again
			bne $s4, $t5, lineLoop

		addi $s2, $s2, 1	# Increment track counter
		li $s4, 0		# Reset value type counter

		li $t0, 2	# $t0 = 2 (number of instrument tracks)
		# If all tracks not processed, loop again
		bne $t0, $s2, trackLoop

	# Generate percussion

	# Generate pitch (drum) that first track will be

	# Save in file as pitch -> duration -> volume
	li $s2, 0	# $s2 = 0 (track counter)
	li $s4, 0	# Value type counter (pitch/volume/duration)

	percussionOneLoop:
		# Generate value between 0 and 46 then add 34
		li $v0, 42	# $v0 = 42 (random int in range)
		li $a0, 0	# $a0 = 0 (randomizer id)
		li $a1, 47	# $a1 = 47 (exclusive upper bound)
		syscall

		addi $s7, $a0, 34	# $t0 = $a0 + 34

		li $s3, 0	# $s3 = 0 (beat counter)

		percussionOneLine:
			li $v0, 42	# $v0 = 42 (random int)
			move $a0, $s4	# $a0 = $t4 (use same randomizer as value type counter)

			beq $s4, $0, generatePercussionPitch	# If generating pitch, jump to section where we generate pitch

			li $t0, 1	# $t0 = 1
			beq $t0, $s4, percussionDurRange	# Process duration differently

			li $a0, 127	# $a1 = 128 (exclusive upper bound)
			j percussionSave

			percussionDurRange:
			li $a0, 2001	# $a1 = 2001 (exclusive upper bound)
			j percussionSave

			generatePercussionPitch:
			# 50% chance note is rest
			li $v0, 42	# $v0 = 42 (random int in range)
			li $a0, 0	# $a0 = 0 (randomizer id)
			li $a1, 2	# $a1 = 2 (exclusive upper bound)
			syscall

			bne $a0, $0, generateRegularPercussionPitch

			li $a0, 1000
			j percussionSave

			generateRegularPercussionPitch:
			move $a0, $s7

			percussionSave:
			# Save value to file
			# $a0 already contains value to convert into chars
			jal toString

			# Params are already set for writeToOutput
			jal writeToOutput

			# Increment beat
			addi $s3, $s3, 1	# $t3 = $t3 + 1

			la $t2, beats	# $t2 = &beats
			lw $t2, 0($t2)	# $t2 = beats

			# If all beats not processed, loop again
			bne $s3, $t2, percussionOneLine

			# Write newline
			jal writeNL

			# Increment value type
			addi $s4, $s4, 1	# $s4 = $s4 + 1
			li $s3, 0	# Reset beat counter

			li $t5, 3	# $t5 = 3 (number of types of values)

			# If all types of values not processed, loop again
			bne $s4, $t5, percussionOneLine

		addi $s2, $s2, 1	# Increment track counter
		li $s4, 0		# Reset value type counter

		li $t0, 2	# $t0 = 2 (number of percussion tracks)
		# If all tracks not processed, loop again
		bne $t0, $s2, percussionOneLoop

	li $v0, 10	# $v0 = 10 (exit)
	syscall

# toString subroutine
# -------------------
#
# params:
# $a0 - word to convert
#
# returns:
# $a0 - first char
# $a1 - second char
# $a2 - third char
# $a3 - fourth char
#
# Example run:
# Input of $a0 = 0127 would give:
# $a0 = '0', $a1 = '1', $a2 = '2', $a3 = '7'
toString:
	bnez $a0, nonzero	# If not zero, skip to the actual algorithm

	# If we get here then the value is zero and our life is easy
	li $a0, 48
	li $a1, 48
	li $a2, 48
	li $a3, 48
	jr $ra

	nonzero:

	move $t0, $a0	# $t0 = $a0 (number to convert)

	li $t1, 1000	# $t1 = 1000
	div $t0, $t1	# $t0 / $t1
	mfhi $t0	# $t0 = $hi (use remainder for rest of conversion)
	mflo $a0	# $a0 = $lo (quotient is first char)
	addi $a0, $a0, 48	# Convert to character encoding of number

	li $t1, 100	# $t1 = 100
	div $t0, $t1	# $t0 / $t1
	mfhi $t0	# $t0 = $hi (use remainder for rest of conversion)
	mflo $a1	# $a1 = $lo (quotient is second char)
	addi $a1, $a1, 48	# Convert to character encoding of number

	li $t1, 10	# $t1 = 10
	div $t0, $t1	# $t0 / $t1
	mfhi $t0	# $t0 = $hi (use remainder for rest of conversion)
	mflo $a2	# $a2 = $lo (quotient is third char)
	addi $a2, $a2, 48	# Convert to character encoding of number

	move $a3, $t0	# $a3 = $t0 (last number)
	addi $a3, $a3, 48	# Convert to character encoding of number

	jr $ra

# writeNL subroutine
# ------------------
#
# params:
# none
#
# returns:
# none
#
# Writes a newline to the output file.
writeNL:
	li $v0, 15	# $v0 = 15 (write to file)
	move $a0, $s1	# $a0 = $s1 (file descriptor)
	la $a1, output	# $a1 = &output
	li $t0, 10	# $t0 = 10 (newline)
	sb $t0, 0($a1)	# Store newline in output buffer
	li $a2, 1	# $a2 = 1 (bytes to write)
	syscall

	jr $ra

# addToRoot subroutine
# --------------------
#
# params:
# $a0 - scale degree
#
# returns:
# $a0 - note pitch
#
# Given a scale degree, gets the pitch value for a note
# relative to the root note.
addToRoot:
	move $t0, $a0		# $t0 = $a0 (scale degree param)
	beq $t0, $0, rootEnd	# If scale degree is 0 (tonic), no adding needed

	la $t1, scale	# $t1 = &scale
	move $t2, $s5	# $t2 = $s5 (result, start at root note)
	li $t3, 0	# $t3 = 0 (counter)

	rootLoop:
		lb $t4, 0($t1)	# $t4 = next interval value

		add $t2, $t2, $t4	# $t2 = $t2 + $t4

		addi $t1, $t1, 1	# Move to next interval
		addi $t3, $t3, 1	# Increment counter

		rootLoopTest:
		bne $t0, $t3, rootLoop

	move $a0, $t2

	jr $ra

	rootEnd:
	move $a0, $s5
	jr $ra

# writeToOutput subroutine
# ------------------------
#
# params:
# $a0 - first char
# $a1 - second char
# $a2 - third char
# $a3 - fourth char
#
# returns:
# none
#
# Writes four chars to the output file.
writeToOutput:
	move $t0, $a0	# $t0 = $a0
	move $t1, $a1	# $t1 = $a1
	move $t2, $a2	# $t2 = $a2
	move $t3, $a3	# $t3 = $a3

	# Move each char into output buffer then write
	la $t4, output	# $t2 = &output

	sb $t0, 0($t4)	# Save char in output buffer

	li $v0, 15	# $v0 = 15 (write to file)
	move $a0, $s1	# $a0 = $s1 (file descriptor)
	la $a1, output	# $a1 = &output
	li $a2, 1	# $a2 = 1 (bytes to write)
	syscall

	sb $t1, 0($t4)	# Save char in output buffer

	li $v0, 15	# $v0 = 15 (write to file)
	move $a0, $s1	# $a0 = $s1 (file descriptor)
	la $a1, output	# $a1 = &output
	li $a2, 1	# $a2 = 1 (bytes to write)
	syscall

	sb $t2, 0($t4)	# Save char in output buffer

	li $v0, 15	# $v0 = 15 (write to file)
	move $a0, $s1	# $a0 = $s1 (file descriptor)
	la $a1, output	# $a1 = &output
	li $a2, 1	# $a2 = 1 (bytes to write)
	syscall

	sb $t3, 0($t4)	# Save char in output buffer

	li $v0, 15	# $v0 = 15 (write to file)
	move $a0, $s1	# $a0 = $s1 (file descriptor)
	la $a1, output	# $a1 = &output
	li $a2, 1	# $a2 = 1 (bytes to write)
	syscall

	jr $ra
