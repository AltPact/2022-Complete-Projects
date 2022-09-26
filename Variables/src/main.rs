fn main() {
    // let mut x = 5;
    // println!("The value of x is: {x}");
    // x = 6;
    // println!("The value of x is: {x}");

    // const THREE_HOURS_IN_SECONDS: u32 = 60 * 60 * 3;

    let x = 5;

    let x = x + 1;

    {
        let x = x * 2;
        println!("The value of x in the inner scope is: {x}");
    }

    println!("The value of x is: {x}");

    let guess: u32 = "42".parse().expect("Not a number!");

    /*
        Sizes in Rust: (Length, Signed, Unsigned)
        8-bit   || i8       || u8
        16-bit  || i16      || u16
        32-bit  || i32      || u32
        64-bit  || i64      || u64
        128-bit || i128     || u128
        arch    || isize    || usize

        Number literals ||	Example
        Decimal	        ||  98_222
        Hex	            ||  0xff
        Octal           ||  0o77
        Binary	        ||  0b1111_0000
        Byte (u8 only)  ||  b'A'
    */

    let x = 2.0; // f64

    let y: f32 = 3.0; // f32

}
