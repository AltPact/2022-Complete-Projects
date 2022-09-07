use std::io;

use prompts::universalCommands;
mod prompts;


fn prompt() -> bool {
    let mut input = String::new();
    io::stdin().read_line(&mut input).unwrap();

    if input.to_ascii_lowercase() == "help"{
        universalCommands(input);
    }


    return input.to_ascii_lowercase().starts_with("y");
}

// pub fn universalCommands() -> (String, bool) {
//     return ("string".to_string(), true);
// }

fn main() {
    prompts::intro();
}
