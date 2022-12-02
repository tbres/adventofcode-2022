use std::collections::HashMap;
use std::vec::Vec;

use rustig::read_lines;

fn main() {

    let path = String::from(r"data\input.txt");
    let rules_beat = HashMap::from([
        ("R", "S"),
        ("S", "P"),
        ("P", "R")
    ]);
    let rules_lose = HashMap::from([
        ("R", "P"),
        ("S", "R"),
        ("P", "S")
    ]);
    let score_map = HashMap::from([
        ("R", 1),
        ("P", 2),
        ("S", 3)
    ]);
    let lose: u32 = 0;
    let draw: u32 = 3;
    let win: u32 = 6;
    let input_map = HashMap::from([
        ("A", "R"),
        ("B", "P"),
        ("C", "S"),
        ("X", "R"),
        ("Y", "P"),
        ("Z", "S")
    ]);

    let mut total_score: u32 = 0;

    if let Ok(lines) = read_lines(path) {
        // Consumes the iterator, returns an (Optional) String
        for line in lines {
            if let Ok(ip) = line {
                // println!("line: {}", ip);
                let entries: Vec<&str> = ip.split(" ").collect();
                let opp_choice = input_map.get(entries[0]).unwrap();
                // let choice = input_map.get(entries[1]).unwrap();
                let choice = match entries[1] {
                    "X" => rules_beat.get(opp_choice).unwrap(),
                    "Y" => opp_choice,
                    "Z" => rules_lose.get(opp_choice).unwrap(),
                    &_ => opp_choice
                };
                let score = score_map.get(choice).unwrap();
                let beats = rules_beat.get(choice).unwrap();
                let mut outcome = lose;
                if choice == opp_choice {
                    outcome = draw;
                } else if opp_choice == beats {
                    outcome = win;
                }
                let round_score = score + outcome;
                total_score += round_score;
            }
        }
    }

    println!("{}", total_score);

}