.data

scale: 		.word 	2, 2, 1, 2, 2, 2, 1

.text

#############
# REGISTERS #
#############
#
# $s2 - track counter / instrument counter
# $s5 - root note
# $s6 - mode
#

main:

	li $s2, 0	# $t1 = 0 (counter)	

	li $s5, 48 	# root note
	li $s6, 0	#mode - Major scale
	la $s7, scale	
	
	
	scaleLoop:
	
		li $v0, 1 # print integer service
		la $a0, 0($s5)	# load root note
		syscall
		li $v0, 11 # print char
		la $a0, 32 # load space character
		syscall

		# TODO: Replace above prints with call to the toString subroutine in the randomizer when we incorporate

		lw $t2, 0($s7) # save first element of scale pattern ( $t2 = scale[0] )
		add $s5, $s5, $t2 # add to root note ( $s5 += $t2 )
		addi $s7, $s7, 4 	# increment array pointer

		addi $s2, $s2, 1 # increment counter ( $s2++ )
		bne $s2, 8, scaleLoop # loop while $s2 != 8

exit:
	li $v0, 10 # load exit
	syscall
