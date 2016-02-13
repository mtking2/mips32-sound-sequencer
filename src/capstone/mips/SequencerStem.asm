.text

#############
# REGISTERS #
#############

# $t0-$t9 :: Temporary registers
#
# $s0 :: file descriptor
# $s1 :: address where instrument data starts
# $s2 :: address where track data starts
# $s3 :: size of each line's data
# $s4 :: address of current track data
# $s6 :: beat counter
# $s7 :: track counter

main:
	## Load track data ##

	# Open data file
	li $v0, 13		# $v0 = syscall 13 (open file)
	la $a0, filename	# $a0 = file name string
	li $a1, 0		# $a1 = 0 (open for read)
	li $a2, 0		# mode (ignored)
	syscall
		
	move $s0, $v0	# $s0 = file descriptor
	
	# Read instruments
	
	# Calculate bytes to read
	# bytes to read = number of tracks * 4 bytes
	la $t0, tracks	# $t0 = &tracks
	lw $t0, 0($t0)	# $t0 = tracks
	li $t1, 4		# $t1 = 4
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $a0	# $a0 = $lo (bytes to read)
	
	# Read from file
	jal fileRead
	
	# Save stack pointer address
	move $s1, $sp	# $s1 = $sp (address where instruments are stored)
	
	# Read track data
	
	# Call fileRead by each line of data
	
	# Calculate lines to read
	# lines to read = tracks * 3 lines of data
	la $t0, tracks	# $t0 = &tracks
	lw $t0, 0($t0)	# $t0 = tracks
	li $t1, 3	# $t1 = 3
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $t9	# $t9 = lines to read
	
	# Calculate size of line data
	# size of line data = beats * 4 bytes
	la $t0, beats	# $t0 = &beats
	lw $t0, 0($t0)	# $t0 = beats
	li $t1, 4		# $t1 = 4
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $t0	# $t0 = $lo (result)

	li $t1, 2	# $t1 = 2
	mult $t1, $t0	# $lo = $t1 * $s3

	mflo $s3	# $s3 = $lo (result)
	
	
	# Read data for each line
	li $s7, 0		# Counter
	
	trackLoop:
		move $a0, $s3	# $a0 = $s3 (bytes to read)

		# Save t values to stack
		# Must compensate for fileRead saving to stack, 
		# so save before where the read data will go

		# have to sub from stack bytes to read + 4
		move $t0, $sp		# Use copy of stack pointer
		
		# Subtract how many bytes are going to be moved onto the stack,
		# and size of the word we are saving
		sub $t0, $t0, $a0	# $t0 = $t0 - $a0
		sw $t9, -4($t0)		# Save $t9 to stack

		jal fileRead

		lw $t9, -4($sp)		# $t9 was saved before where $sp moved to
		
		addi $s7, $s7, 1	# Increment counter
		
		# Loop again for each line
		bne $s7, $t9, trackLoop
	
	# Save stack pointer address
	move $s2, $sp	# $s2 = $sp (address where track data begins)
	
	# Close data file
	li $v0, 16			# $v0 = syscall 16 (close file)
	add $a0, $s0, $0	# $a0 = $s0 (file descriptor)
	syscall
	
	## Configure MIDI channels ##
	
	li $s7, 0	# Counter
	
	channelLoop:
		li $v0, 38	# $v0 = syscall 38 (MIDI program change)
		move $a0, $s7	# $a0 = $s7 (channel number)
		
		# Get instrument value
		# Address:
		#	instrument data address + (size of word * current track)
		li $t0, 4		# $t0 = 4 (size of word)
		mult $t0, $s7	# $lo = $t0 * $s7 (current track)
		
		mflo $t0	# $t0 = $lo (result)
		
		add $a1, $s1, $t0	# $a1 = $s1 (instrument data address) + $t0 (offset)
		lw $a1, 0($a1)		# $a1 = instrument
		
		# Configure channel
		syscall
		
		addi $s7, $s7, 1	# Increment counter
	
		la $t0, tracks	# $t0 = &tracks
		lw $t0, 0($t0)	# $t0 = tracks
		
		bne $s7, $t0, channelLoop
	
	## Play notes ##
	
play:
	li $s6, 0	# Beat counter
	
	beatLoop:
		li $s7, 0	# Track counter
		
		playNote:
			# Calculate address for track
			# Address = 
			# track data address + 
			# [(size of each line's data * 3) * track counter]
			li $t0, 3		# $t0 = 3
			mult $t0, $s3	# $lo = $s3 (size of each line's data) * $t0
			
			mflo $t0	# $t0 = $lo (result)
			
			mult $s7, $t0	# $lo = $s7 (track counter) * $t0 (size of each track's data)
			
			mflo $t0		# $t0 = $lo (result)
			
			add $s4, $s2, $t0	# $s4 = $s2 (track data address) + $t0 (offset)
			
			# Move to correct volume (stored as volume data -> duration data -> pitch data)
			# =	address + (current beat * size of word) 
			# 	+ (3 * size of each line's data * current track)
			li $t0, 4	# $t0 = 4 (size of word)
			mult $t0, $s6	# $lo = $t0 * $s6 (beat counter)
			
			mflo $t0	# $t0 = $lo (result)
			
			li $t1, 3		# $t1 = 3
			mult $t1, $s7	# $lo = $t1 * $s7 (track counter)
			
			mflo $t1	# $t1 = $lo (result)
			
			mult $t1, $s3	# $lo = $t1 (3 * current track) * $s3 (size of each line's data)
			
			mflo $t1	# $t1 = $lo (result)
			
			add $s4, $s4, $t0	# $s4 = $s4 (current track address) + $t0 (beat offset)
			add $s4, $s4, $t1	# $s4 = $s4 (current track + beat address) + $t1 (data offset)
			
			# Set params
			
			move $a3, $s4	# $a3 = &volume
			lw $a3, 0($a3)	# $a3 = volume
			
			add $s4, $s4, $s3	# $s4 = $s4 + $s3 (size of each line's data)
			
			move $a1, $s4	# $a1 = &duration
			lw $a1, 0($a1)	# $a1 = duration
			
			add $s4, $s4, $s3	# $s4 = $s4 + $s3 (size of each line's data)
			
			move $a0, $s4	# $a0 = &pitch
			lw $a0, 0($a0)	# $a0 = pitch
			
			# If pitch is 1000, this is a rest
			li $t9, 1000
			beq $a0, $t9, sleep
			
			# Load channel (track number)
			move $a2, $s7
			
			li $v0, 37	# $v0 = syscall 37 (MIDI out)
			syscall
			
			addi $s7, $s7, 1	# Increment track counter
			
			la $t0, tracks	# $t0 = &tracks
			lw $t0, 0($t0)	# $t0 = tracks
			
			bne $t0, $s7, playNote	# If all tracks played, go to next beat
			
			# LOOP END #
			
sleep:
		## Sleep until next beat ##
		
		li $v0, 32		# $v0 = syscall 32 (sleep)
		
		# Load time to wait
		la $a0, timeToWait	# $a0 = &timeToWait
		lw $a0, 0($a0)		# $a0 = timeToWait
		
		syscall
			
		addi $s6, $s6, 1	# Increment beat counter
		
		la $t0, beats	# $t0 = &beats
		lw $t0, 0($t0)	# $t0 = beats
		
		bne $t0, $s6, beatLoop	# If more beats to play, go to next beat
		j play	# Loop until process is ended
	
# fileRead subroutine
# -------------------
# Saves data from the dedicated data file onto the stack, looping
# once for each track in the sequencer.  Skips one char after reading, 
# to compensate for newline.
#
# Parameters
# ----------
# $a0		: bytes to read
fileRead:
	move $t0, $0	# $t0 = 0 (bytes read count)
	move $t1, $a0	# $t1 = $a0 (bytes to read)
	
	# Subtract bytes to read from stack pointer
	sub $sp, $sp, $t1
	
	move $t2, $sp	# Get copy of stack pointer address to save words to

	fileLoop:
		# Read from file 4 chars at a time
		li $v0, 14			# $v0 = syscall 14 (read from file)
		move $a0, $s0		# $a0 = $s0 (file descriptor)
		la $a1, input		# $a1 = address of input buffer
		li $a2, 1			# $a2 = 1 (max bytes to read)
		syscall
		
		la $t6, input	# $t6 = &input
		lb $t6, 0($t6)	# $t6 = input
		
		li $v0, 14			# $v0 = syscall 14 (read from file)
		move $a0, $s0		# $a0 = $s0 (file descriptor)
		la $a1, input		# $a1 = address of input buffer
		li $a2, 1			# $a2 = 1 (max bytes to read)
		syscall
		
		la $t7, input	# $t7 = &input
		lb $t7, 0($t7)	# $t7 = input
		
		li $v0, 14			# $v0 = syscall 14 (read from file)
		move $a0, $s0		# $a0 = $s0 (file descriptor)
		la $a1, input		# $a1 = address of input buffer
		li $a2, 1			# $a2 = 1 (max bytes to read)
		syscall
		
		la $t8, input	# $t8 = &input
		lb $t8, 0($t8)	# $t8 = input
		
		li $v0, 14			# $v0 = syscall 14 (read from file)
		move $a0, $s0		# $a0 = $s0 (file descriptor)
		la $a1, input		# $a1 = address of input buffer
		li $a2, 1			# $a2 = 1 (max bytes to read)
		syscall
		
		la $t9, input	# $t9 = &input
		lb $t9, 0($t9)	# $t9 = input
		
		# $t6 - $t9 == chars that were read
		# Set params for parseInt
		move $a0, $t6	# $a0 = $t6
		move $a1, $t7	# $a1 = $t7
		move $a2, $t8	# $a2 = $t8
		move $a3, $t9	# $a3 = $t9

		# Save t register values, $ra on stack
		li $t4, 4		# $t4 = 4 (size of word)

		sub $sp, $sp, $t4	# $sp = $sp - $t4
		sw $t0, 0($sp)		# Save $t0 on stack

		sub $sp, $sp, $t4	# $sp = $sp - $t4
		sw $t1, 0($sp)		# Save $t1 on stack

		sub $sp, $sp, $t4	# $sp = $sp - $t4
		sw $t2, 0($sp)		# Save $t2 on stack

		sub $sp, $sp, $t4	# $sp = $sp - $t4
		sw $ra, 0($sp)		# Save $ra on stack
		
		jal parseInt

		move $t9, $a0	# $t9 = $a0 (result)

		# Re-load t values, $ra
		lw $ra, 0($sp)		# Load $ra from stack
		addi $sp, $sp, 4	# Add 4 to stack

		lw $t2, 0($sp)		# Load $t2 from stack
		addi $sp, $sp, 4	# $sp = $sp + 4

		lw $t1, 0($sp)		# Load $t2 from stack
		addi $sp, $sp, 4	# $sp = $sp + 4

		lw $t0, 0($sp)		# Load $t2 from stack
		addi $sp, $sp, 4	# $sp = $sp + 4
		
		# Save result to stack
		sw $t9, 0($t2)
		
		# Move up the stack to next word address
		addi $t2, $t2, 4	# $t2 = $t2 + 4
		addi $t0, $t0, 4	# Increment counter by 4 (size of word)
		bne $t0, $t1, fileLoop	# If all bytes not processed, loop again
	
	# Skip newline
	li $v0, 14		# $v0 = syscall 14 (read from file)
	move $a0, $s0		# $a0 = $s0 (file descriptor)
	la $a1, input		# $a1 = input
	li $a2, 1		# $a2 = 1 (max bytes to read)
	syscall
	
	# Return
	jr $ra
	
# parseInt subroutine
# -------------------
# Changes four chars into an integer value.
#
# Parameters
# ----------
# $a0-$a3	: the chars to convert
#
# Returns
# -------
# $a0		: the parsed integer value
parseInt:
	# Subtract 48 from character code to get integer values
	li $t0, 48			# $t0 = 48
	sub $a0, $a0, $t0	# $a0 = $a0 - $t0
	sub $a1, $a1, $t0	# $a1 = $a1 - $t0
	sub $a2, $a2, $t0	# $a2 = $a2 - $t0
	sub $a3, $a3, $t0	# $a3 = $a3 - $t0
	
	# Add powers of 10 to return value
	move $t0, $a3	# $t0 = $a3 (last num is already correct power)
	
	li $t1, 1000		# $t1 = 1000
	mult $t1, $a0		# $lo = $t1 * $a0
	mflo $t1		# $t1 = $lo	(result)
	
	add $t0, $t0, $t1	# $t0 = $t0 + $t1
	
	li $t1, 100		# $t1 = 100
	mult $t1, $a1		# $lo = $t1 * $a1
	mflo $t1		# $t1 = $lo	(result)
	
	add $t0, $t0, $t1	# $t0 = $t0 + $t1
	
	li $t1, 10		# $t1 = 10
	mult $t1, $a2		# $lo = $t1 * $a2
	mflo $t1		# $t1 = $lo	(result)
	
	add $t0, $t0, $t1	# $t0 = $t0 + $t1
	
	move $a0, $t0	# $a0 = $t0 (return value)
	jr $ra