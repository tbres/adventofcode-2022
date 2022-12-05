use std::vec::Vec;
use std::collections::HashMap;
use std::convert::TryFrom;

use lazy_static::lazy_static;
use regex::Regex;
use num_integer::div_floor;
use itertools::Itertools;

use rustig::read_lines;

fn main() {

    // let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");

    let mut stacks = parse_stacks(&path);
    let moves = parse_moves(&path);

    // for [n, from, to] in moves {
    //     for _ in 0..n {
    //         let crat = stacks.get_mut(&from).unwrap().pop().unwrap();
    //         stacks.get_mut(&to).unwrap().push(crat);
    //     }
    // }

    for [n, from, to] in moves {
        let mut stack = stacks.get_mut(&from).unwrap();
        let cutoff = stack.len() - usize::try_from(n).unwrap();
        let mut crates = stack.split_off(cutoff);
        stacks.get_mut(&to).unwrap().append(&mut crates);
    }

    let mut upper: String = "".to_owned();
    for key in stacks.keys().sorted() {
        let stack = stacks.get(&key).unwrap();
        upper.push_str(&stack[stack.len() - 1]);
    }
    println!("{}", upper);

}

fn parse_stacks(path: &String) -> HashMap<u32, Vec<String>> {
    lazy_static! {
        static ref CRAT_RE: Regex = Regex::new(r"\[([A-Z]{1})\]").unwrap();
    }
    let mut stacks: HashMap<u32, Vec<String>> = HashMap::new();
    for line in read_lines(path).unwrap() {
        let line = line.unwrap();
        if !line.contains("[") {
            break;
        }
        let length = line.len();
        println!("line lenght: {}", length);
        for i in (0..length-2).step_by(4) {
            let mut right = i + 4;
            if right > length {
                right = length;
            }
            let column: u32 = (div_floor(i, 4) + 1).try_into().unwrap();
            let crat = line[i..right].to_string();
            if CRAT_RE.is_match(&crat) {
                let caps = CRAT_RE.captures(&crat).unwrap();
                let letter = caps.get(1).unwrap().as_str().to_string();
                if !stacks.contains_key(&column) {
                    stacks.insert(column,  Vec::new());
                }
                stacks.get_mut(&column).unwrap().push(letter);
            }
        }
    }
    for (_, val) in stacks.iter_mut() {
        val.reverse();
    }    
    println!("{:?}", stacks);
    stacks
}

fn parse_moves(path: &String) -> Vec<[u32; 3]> {
    lazy_static! {
        static ref MOVE_RE: Regex = Regex::new(r"move (?P<n>\d+) from (?P<f>\d+) to (?P<t>\d+)").unwrap();
    }
    let mut moves = vec![];
    for line in read_lines(path).unwrap() {
        let line = line.unwrap();
        if MOVE_RE.is_match(&line) {
            let caps = MOVE_RE.captures(&line).unwrap();
            let n: u32 = caps.name("n").unwrap().as_str().parse().unwrap();
            let from: u32 = caps.name("f").unwrap().as_str().parse().unwrap();
            let to: u32 = caps.name("t").unwrap().as_str().parse().unwrap();
            println!("move {} from {} to {}", n, from, to);
            moves.push([n, from, to]);
        }
    }
    moves
}