.text

#############
# REGISTERS #
#############

# $t0-$t9 :: Temporary registers
#
# $s0 :: file descriptor
# $s1 :: address where instrument data starts
# $s2 :: address where track data starts
# $s3 :: size of each track's data
# $s4 :: address of current track data
# $s5 :: size of track data for each individual value (pitch, volume, duration)
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
	
	# Calculate bytes to read
	# bytes to read = beats * 4 bytes * 3 sets of data (pitch, volume, duration)
	la $t0, beats	# $t0 = &beats
	lw $t0, 0($t0)	# $t0 = beats
	li $t1, 4		# $t1 = 4
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $t0		# $t0 = $lo (result)
	li $t1, 3		# $t1 = 3
	mult $t0, $t1	# $lo = $t0 * $t1
	
	mflo $s3	# $s3 = bytes to read (size of track data)
	
	# Read data for each track
	li $s7, 0		# Counter
	
	trackLoop:
		addi $s7, $s7, 1	# Increment counter
		
		move $a0, $s3	# $a0 = $s3 (bytes to read)
		jal fileRead
		
		la $t0, tracks	# $t0 = &tracks
		lw $t0, 0($t0)	# $t0 = tracks
		bne $s7, $t0, trackLoop
	
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
	
	# Calculate size of data value array
	# = size of word * number of beats
	li $t0, 4		# $t0 = 4 (size of word)
	la $t1, beats	# $t1 = &beats
	lw $t1, 0($t1)	# $t1 = beats
	mult $t0, $t1	# $lo = $t0 * $t1
			
	mflo $s5	# $s5 = $lo (result)

	li $s6, 0	# Beat counter
	
	beatLoop:
	
		li $s7, 0	# Track counter
		
		playNote:
			# Calculate address for track
			# Address = 
			# track data address + 
			# (size of each track's data * which track is current)
			mult $s7, $s3	# $lo = $s7 (track counter) * $s3 (size of track data)
			mflo $t0	# $t0 = $lo (result)
			
			add $s4, $s2, $t0	# $s4 = $s2 (track data address) + $t0 (offset)
			
			# Move to correct pitch
			# 	address + (current beat * size of word)
			li $t0, 4		# $t0 = 4 (size of word)
			mult $t0, $s6	# $lo = $t0 * $s6 (beat number)
			
			mflo $t0	# $t0 = $lo (result)
			add $s4, $s4, $t0	# $s4 = $s4 (current track address) + $t0 (beat offset)
			
			# Set params
			
			move $a0, $s4	# $a0 = &pitch
			lw $a0, 0($a0)	# $a0 = pitch
			
			add $s4, $s4, $s5	# $s4 = $s4 + $s5 (size of data value array)
			
			move $a1, $s4	# $a0 = &duration
			lw $a1, 0($a1)	# $a1 = duration
			
			add $s4, $s4, $t0	# $s4 = $s4 + $s5 (size of data value array)
			
			move $a3, $s4	# $a3 = &volume
			lw $a3, 0($a3)	# $a3 = volume
			
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
# once for each track in the sequencer.
#
# Parameters
# ----------
# $a0		: bytes to read
fileRead:
	move $t0, $0	# $t0 = 0 (loop count)
	move $t1, $a0	# $t1 = $a0 (bytes to read)
	
	la $t2, tracks	# $t2 = &tracks
	lw $t2, 0($t2)	# $t2 = tracks

	fileLoop:
		# Substract stack pointer by bytes to read
		sub $sp, $sp, $t1	# $sp = $sp - $t1
		
		move $t3, $sp	# Use $t3 to point to where data goes on stack
		
		# Read from file directly onto stack
		li $v0, 14			# $v0 = syscall 14 (read from file)
		move $a0, $s0		# $a0 = $s0 (file descriptor)
		move $a1, $t3		# $a1 = address of input buffer (stack)
		move $a2, $t1		# $a2 = $t1 (max bytes to read)
		syscall
		
		addi $t0, $t0, 1	# Increment counter
		bne $t0, $t2, fileLoop	# If all tracks not processed, loop again
	
	# Return
	jr $ra
