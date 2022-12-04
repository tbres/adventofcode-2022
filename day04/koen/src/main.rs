use std::vec::Vec;

use rustig::read_lines;

fn main() {

    // let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");

    let mut within_count: u32 = 0;
    let mut overlap_count: u32 = 0;
    for line in read_lines(path).unwrap() {
        let line = line.unwrap();
        let pair = parse_line(&line);
        println!("{:?}", pair);
        let within = range_compare(&pair);
        println!("{}", within);
        if within {
            within_count += 1;
        }
        let overlap = overlap_compare(&pair);
        println!("{}", overlap);
        if overlap {
            overlap_count += 1;
        }
    }
    println!("range contained: {}", within_count);
    println!("range overlap: {}", overlap_count);
}

fn parse_line(line: &str) -> Pair {
    let pair: Vec<&str> = line.split(",").collect();
    let left: Vec<&str> = pair[0].split("-").collect();
    let right: Vec<&str> = pair[1].split("-").collect();
    let left_section = Section {
        min: left[0].parse().unwrap(),
        max: left[1].parse().unwrap()
    };
    let right_section = Section {
        min: right[0].parse().unwrap(),
        max: right[1].parse().unwrap()
    };
    Pair {
        left: left_section,
        right: right_section
    }
}

#[derive(Debug)]
struct Pair {
    left: Section,
    right: Section
}

#[derive(Debug)]
struct Section {
    min: u32,
    max: u32
}

fn range_compare(pair: &Pair) -> bool {
    let mut within: bool = false;
    if pair.left.min <= pair.right.min && pair.left.max >= pair.right.max {
        within = true;
    } else if pair.right.min <= pair.left.min && pair.right.max >= pair.left.max {
        within = true;
    }
    within
}

fn overlap_compare(pair: &Pair) -> bool {
    let mut overlap: bool = true;
    if pair.left.max < pair.right.min {
        overlap = false;
    } else if pair.right.max < pair.left.min {
        overlap = false;
    }
    overlap
}