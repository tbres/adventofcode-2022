use std::fs;
use std::vec::Vec;
use std::collections::HashMap;

type Packet = HashMap<Vec<u32>, i32>;
type Pair = Vec<Packet>;

fn main() {
    let path = String::from(r"data\input_test.txt");
    // let path = String::from(r"data\input.txt");
    let pairs = parse_input(&path);
    for pair in pairs {
        for packet in pair {
            let mut keys: Vec<&Vec<u32>> = packet.keys().collect();
            keys.sort();
            println!("{:?}", keys);
            println!("{:?}", packet);
        }
        println!();
    }
}

fn parse_input(path: &String) -> Vec<Pair> {
    let mut pairs: Vec<Pair> = vec![];
    let mut pair: Pair = vec![]; 
    let text = fs::read_to_string(path).expect("Error reading the file");
    for line in text.lines() {
        if !line.is_empty() {
            let mut packet: Packet = HashMap::new();
            let mut current_index: Vec<u32>= vec![];
            let mut has_value: bool = false;
            let mut current_value = String::from("");
            for c in line.chars() {
                if c == '[' {
                    current_index.push(0);
                    has_value = false;
                } else if c == ',' {
                    if !current_value.is_empty() {
                        let current_number: i32 = current_value.parse().unwrap();
                        packet.insert(current_index.to_vec(), current_number);
                        current_value.clear();
                    }
                    let last = current_index.len() - 1;
                    current_index[last] += 1;
                } else if c == ']' {
                    if !current_value.is_empty() {
                        let current_number: i32 = current_value.parse().unwrap();
                        packet.insert(current_index.to_vec(), current_number);
                        current_value.clear();
                    } else if !has_value {
                        packet.insert(current_index.to_vec(), -1);
                        has_value = true;
                    }
                    current_index.pop();
                } else {
                    has_value = true;
                    current_value.push(c);
                }
            }
            pair.push(packet);
        } else {
            pairs.push(pair.to_vec());
            pair.clear();
        }
    }
    pairs
}