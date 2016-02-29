.data

filename:	.asciiz	"randomized.asm"
output:		.space	4
beats:		.space	1

.text

#############
# REGISTERS #
#############
#
# $s0 - size of line data

main:
	# Set randomizer ranges
	# Upper bound is exclusive

	# Randomizer id's:
	# 0 - random from 0-127		(pitch)
	# 1 - random from 0-2000	(duration)
	# 2 - random from 0-127		(volume)
	# 3 - random from 0-1		(boolean)

	li $v0, 42	# $v0 = 42 (set randomizer range)
	li $a0, 0	# $a0 = 0 (randomizer id)
	li $a1, 128	# $a1 = 128 (upper bound)
	syscall

	li $v0, 42	# $v0 = 42 (set randomizer range)
	li $a0, 1	# $a0 = 1 (randomizer id)
	li $a1, 2001	# $a1 = 2001 (upper bound)
	syscall

	li $v0, 42	# $v0 = 42 (set randomizer range)
	li $a0, 2	# $a0 = 1 (randomizer id)
	li $a1, 128	# $a1 = 128 (upper bound)
	syscall

	li $v0, 42	# $v0 = 42 (set randomizer range)
	li $a0, 3	# $a0 = 3 (randomizer id)
	li $a1, 2	# $a1 = 2 (upper bound)
	syscall
	
	# Randomize instruments
	li $t0, 4	# $t0 = 4 (number of tracks)
	li $t1, 0	# $t1 = 0 (counter)

	instLoop:
		li $v0, 41	# $v0 = 41 (random int)
		li $a0, 0	# $a0 = 0 (randomizer id)
		syscall

		# Move stack pointer by 4 (size of word)
		# Since $t0 is already 4, we can re-use the register
		sub $sp, $sp, $t0	# $sp = $sp - $t0

		# Save result to stack
		sw $a0, 0($sp)

		addi $t1, $t1, 1	# Increment counter

		# If not all instruments processed, loop again
		bne $t0, $t1, instLoop

	# Randomize each line of data
	# Save on stack as pitch -> duration -> volume
	
	li $t0, 4	# $t0 = 4 (number of tracks)
	li $t1, 0	# $t1 = 0 (track counter)
	la $t2, beats	# $t2 = &beats
	lw $t2, 0($t2)	# $t2 = beats

	li $t4, 0	# Value type counter (pitch/volume/duration)
	li $t5, 3	# Types of values

	trackLoop:
		li $t3, 0	# $t3 = 0 (beat counter)

		lineLoop:
			li $v0, 41	# $v0 = 41 (random int)
			move $a0, $t4	# $a0 = $t4 (randomizer id is same as value type counter)
			syscall

			# Save value to stack
			subi $sp, $sp, 4	# Move stack pointer by one word
			sw $a0, 0($sp)		# Save randomly generated number to stack

			# Increment beat
			addi $t3, $t3, 1	# $t3 = $t3 + 1

			# If all beats not processed, loop again
			bne $t3, $t2, lineLoop

			# Increment value type
			addi $t4, 1	# $t4 = $t4 + 1
			li $t3, 0	# Reset beat counter

			# If all types of values not processed, loop again
			bne $t4, $t5, lineLoop
		
		addi $t1, $t1, 1	# Increment track counter
		li $t4, 0		# Reset value type counter

		# If all tracks not processed, loop again
		bne $t0, $t1, trackLoop

	# TODO Save values to file from stack

	li $v0, 10	# $v0 = 10 (exit)
	syscall