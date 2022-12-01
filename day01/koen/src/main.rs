use std::fs::File;
use std::io::{self, BufRead};
use std::path::Path;
use std::vec;

fn main() {

    let path = String::from(r"data\input.txt");
    let mut elf_calories = vec![];

    if let Ok(lines) = read_lines(path) {
        // Consumes the iterator, returns an (Optional) String
        let mut total_calories:u32 = 0;
        for line in lines {
            if let Ok(ip) = line {
                // println!("line: {}", ip);
                let calories:u32 = match ip.parse() {
                    Ok(res) => res,
                    Err(_e) => 0
                };
                // println!("parsed: {}", calories);
                total_calories = total_calories + calories;
                // println!("total: {}", total_calories);
                if calories == 0 {
                    // println!("new elf");
                    elf_calories.push(total_calories);
                    total_calories = 0;
                }
            }
        }
    }

    println!("{:?}", elf_calories);
    
    let max_calories = elf_calories.iter().max().unwrap();
    println!("max: {}", max_calories);

    elf_calories.sort();
    elf_calories.reverse();
    let top_three = &elf_calories[..3];
    println!("top 3: {:?}", top_three);
    let sum: u32 = top_three.iter().sum();
    println!("top 3 sum: {}", sum)
}

fn read_lines<P>(filename: P) -> io::Result<io::Lines<io::BufReader<File>>>
where P: AsRef<Path>, {
    let file = File::open(filename)?;
    Ok(io::BufReader::new(file).lines())
}