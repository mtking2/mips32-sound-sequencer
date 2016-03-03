.data

filename:	.asciiz	"C:\\Users\\Brad\\randomized.mss"
output:		.space	1
beats:		.word	16

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

main:
	# Open file for writing
	li $v0, 13		# $v0 = 13 (open file)
	la $a0, filename	# $a0 = name of file
	li $a1, 1		# $a1 = 1 (open for write/create)
	li $a2, 0		# $a2 = 0 (ignored)
	syscall

	move $s1, $v0	# $s1 = file descriptor
	
	# Randomize instruments
	li $s2, 0	# $t1 = 0 (counter)

	instLoop:
		li $v0, 42	# $v0 = 42 (random int in range)
		li $a0, 0	# $a0 = 0 (randomizer id)
		li $a1, 128	# $a1 = 128 (exclusive upper bound)
		syscall

		# Save to file
		# $a0 already contains result
		jal toString

		move $t6, $a0	# $t6 = first char
		move $t7, $a1	# $t7 = second char
		move $t8, $a2	# $t8 = third char
		move $t9, $a3	# $t9 = fourth char

		# Move each char into output buffer then write
		la $t2, output	# $t2 = &output

		sb $t6, 0($t2)	# Save char in output buffer

		li $v0, 15	# $v0 = 15 (write to file)
		move $a0, $s1	# $a0 = $s1 (file descriptor)
		la $a1, output	# $a1 = &output
		li $a2, 1	# $a2 = 1 (bytes to write)
		syscall

		sb $t7, 0($t2)	# Save char in output buffer

		li $v0, 15	# $v0 = 15 (write to file)
		move $a0, $s1	# $a0 = $s1 (file descriptor)
		la $a1, output	# $a1 = &output
		li $a2, 1	# $a2 = 1 (bytes to write)
		syscall

		sb $t8, 0($t2)	# Save char in output buffer

		li $v0, 15	# $v0 = 15 (write to file)
		move $a0, $s1	# $a0 = $s1 (file descriptor)
		la $a1, output	# $a1 = &output
		li $a2, 1	# $a2 = 1 (bytes to write)
		syscall

		sb $t9, 0($t2)	# Save char in output buffer

		li $v0, 15	# $v0 = 15 (write to file)
		move $a0, $s1	# $a0 = $s1 (file descriptor)
		la $a1, output	# $a1 = &output
		li $a2, 1	# $a2 = 1 (bytes to write)
		syscall

		addi $s2, $s2, 1	# Increment counter

		li $t0, 4	# $t0 = 4 (number of tracks)
		# If not all instruments processed, loop again
		bne $t0, $s2, instLoop

	# Write newline
	jal writeNL

	# Randomize each line of data
	# Save in file as pitch -> duration -> volume
	li $s2, 0	# $s2 = 0 (track counter)
	li $s4, 0	# Value type counter (pitch/volume/duration)

	trackLoop:
		li $s3, 0	# $s3 = 0 (beat counter)

		lineLoop:
			li $v0, 42	# $v0 = 42 (random int)
			move $a0, $s4	# $a0 = $t4 (use same randomizer as value type counter)

			li $t0, 1	# $t0 = 1
			beq $t0, $s4, durRange	# Process duration differently

			li $a1, 128	# $a1 = 128 (exclusive upper bound)
			j call

			durRange:
			li $a1, 2001	# $a1 = 2001 (exclusive upper bound)
			
			call:
			syscall

			# Save value to file
			# $a0 already contains value to convert into chars
			jal toString

			move $t6, $a0	# $t6 = first char
			move $t7, $a1	# $t7 = second char
			move $t8, $a2	# $t8 = third char
			move $t9, $a3	# $t9 = fourth char

			# Move each char into output buffer then write
			la $t2, output	# $t2 = &output

			sb $t6, 0($t2)	# Save char in output buffer

			li $v0, 15	# $v0 = 15 (write to file)
			move $a0, $s1	# $a0 = $s1 (file descriptor)
			la $a1, output	# $a1 = &output
			li $a2, 1	# $a2 = 1 (bytes to write)
			syscall

			sb $t7, 0($t2)	# Save char in output buffer

			li $v0, 15	# $v0 = 15 (write to file)
			move $a0, $s1	# $a0 = $s1 (file descriptor)
			la $a1, output	# $a1 = &output
			li $a2, 1	# $a2 = 1 (bytes to write)
			syscall

			sb $t8, 0($t2)	# Save char in output buffer

			li $v0, 15	# $v0 = 15 (write to file)
			move $a0, $s1	# $a0 = $s1 (file descriptor)
			la $a1, output	# $a1 = &output
			li $a2, 1	# $a2 = 1 (bytes to write)
			syscall

			sb $t9, 0($t2)	# Save char in output buffer

			li $v0, 15	# $v0 = 15 (write to file)
			move $a0, $s1	# $a0 = $s1 (file descriptor)
			la $a1, output	# $a1 = &output
			li $a2, 1	# $a2 = 1 (bytes to write)
			syscall

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

		li $t0, 4	# $t0 = 4 (number of tracks)
		# If all tracks not processed, loop again
		bne $t0, $s2, trackLoop

	li $v0, 10	# $v0 = 10 (exit)
	syscall

# toString subroutine
# -------------------
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