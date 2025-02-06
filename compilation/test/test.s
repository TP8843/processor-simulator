	.file	"test.c"
	.option nopic
	.attribute arch, "rv32i2p1"
	.attribute unaligned_access, 0
	.attribute stack_align, 16
	.text
	.align	2
	.globl	square
	.type	square, @function
square:
	slli	a0,a0,1
	ret
	.size	square, .-square
	.align	2
	.globl	main
	.type	main, @function
main:
	addi	sp,sp,-16
	li	a5,8
	sw	a5,12(sp)
	li	a0,0
	addi	sp,sp,16
	jr	ra
	.size	main, .-main
	.ident	"GCC: (gc891d8dc23e) 13.2.0"
