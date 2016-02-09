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
# $s8 :: time (in ms) to wait between beats

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
	li $t1, 3		# $t1 = 3
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $t9	# $t9 = lines to read
	
	# Calculate size of line data
	# size of line data = beats * 4 bytes
	la $t0, beats	# $t0 = &beats
	lw $t0, 0($t0)	# $t0 = beats
	li $t1, 4		# $t1 = 4
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $s3	# $s3 = size of line data
	
	# Read data for each line
	li $s7, 0		# Counter
	
	trackLoop:
		move $a0, $s3	# $a0 = $s3 (bytes to read)
		jal fileRead
		
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
		li $v0, 38		# $v0 = syscall 38 (MIDI program change)
		move $a0, $s7	# $a0 = $s7 (channel number)
		
		# Get instrument value
		# Address:
		#	instrument data address + (size of word * current track)
		li $t0, 4		# $t0 = 4 (size of word)
		mult $t0, $s7	# $lo = $t0 * $s7 (current track)
		
		mflo $t0	# $t0 = $lo (result)
		
		add $a1, $s1, $t0	# $a1 = $s1 (instrument data address) + $t0 (offset)
		
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
			# [(size of each line's data * 3) * which track is current]
			li $t0, 3		# $t0 = 3
			mult $t0, $s3	# $lo = $s3 (size of each line's data) * $t0
			
			mflo $t0	# $t0 = $lo (result)
			
			mult $s7, $t0	# $lo = $s7 (track counter) * $t0 (size of each track's data)
			
			mflo $t0		# $t0 = $lo (result)
			
			add $s4, $s2, $t0	# $s4 = $s2 (track data address) + $t0 (offset)
			
			# Move to correct volume (stored as volume data -> duration data -> pitch data)
			# =	address + (current beat * size of word) 
			# 	+ (3 * size of each line's data * current track)
			li $t0, 4		# $t0 = 4 (size of word)
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
			
			move $a0, $s4	# $a3 = &pitch
			lw $a0, 0($a0)	# $a3 = pitch
			
			# Load channel (track number)
			move $a2, $s7
			
			li $v0, 33	# $v0 = syscall 33 (MIDI out)
			syscall
			
			addi $s7, $s7, 1	# Increment track counter
			
			la $t0, tracks	# $t0 = &tracks
			lw $t0, 0($t0)	# $t0 = tracks
			
			bne $t0, $s7, playNote	# If all tracks played, go to next beat
			
			# LOOP END #
			
		## Sleep until next beat ##
		
		li $v0, 32		# $v0 = syscall 32 (sleep)
		
		# Load time to wait
		la $a0, timeToWait	# $a0 = &timeToWait
		lw $a0, 0($a0)		# $a0 = timeToWait
		
		syscall
			
		addi $s6, $s6, 1	# Increment beat counter
		
		la $t0, beats	# $t0 = &beats
		lw $t0, 0($t0)	# $t0 = beats
		
		bne $t0, $s6, beatLoop	# If all beats played, go back to first beat
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
	move $t0, $0	# $t0 = 0 (loop count)
	move $t1, $a0	# $t1 = $a0 (bytes to read)
	
	# Subtract bytes to read from stack pointer
	sub $sp, $sp, $t1
	
	move $t6, $sp	# Get copy of stack pointer address to save words to

	fileLoop:
		# Read from file
		li $v0, 14			# $v0 = syscall 14 (read from file)
		move $a0, $s0		# $a0 = $s0 (file descriptor)
		la $a1, input		# $a1 = address of input buffer
		li $a2, 4			# $a2 = 4 (max bytes to read)
		syscall
		
		la $t3, input	# $t3 = &input
		lw $t3, 0($t3)	# $t3 = input
		
		la $t5, diff	# $t5 = &diff
		lw $t5, 0($t5)	# $t5 = diff
		
		# Change character code to decimal value
		sub $t3, $t3, $t5	# $t3 = $t3 - $t5
		
		# Save to stack
		sw $t3, 0($t6)
		
		# Move up the stack to next word address
		addi $t6, 4		# $t6 = $t6 + 4
		
		addi $t0, $t0, 4	# Increment counter by 4 (size of word)
		bne $t0, $t1, fileLoop	# If all bytes not processed, loop again
	
	# Skip newline
	li $v0, 14			# $v0 = syscall 14 (read from file)
	move $a0, $s0		# $a0 = $s0 (file descriptor)
	move $a1, $0		# $a1 = $0 (throw away the newline)
	li $a2, 1			# $a2 = 1 (max bytes to read)
	syscall
	
	# Return
	jr $ra
