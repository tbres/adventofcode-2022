use std::fs;
use std::vec::Vec;
use std::collections::HashMap;

type Packet = HashMap<Vec<u32>, i32>;
type Pair = Vec<Packet>;

const EMPTY_LIST: i32 = -1;
const LIST: i32 = -2;

fn main() {
    // let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");
    let pairs = parse_input(&path);
    // print_parsed(&pairs);
    let index_sum = compare(&pairs);
    println!("right order sum: {}", index_sum);
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
                        packet.insert(current_index.to_vec(), EMPTY_LIST);
                        has_value = true;
                    }
                    current_index.pop();
                    if has_value {
                        if !current_index.is_empty() {
                            packet.insert(current_index.to_vec(), LIST);
                        }
                    }
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
    if !pair.is_empty() {
        pairs.push(pair.to_vec());
    }
    pairs
}

fn print_parsed(pairs: &Vec<Pair>) {
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

fn compare(pairs: &Vec<Pair>) -> usize {
    let mut right_order: Vec<usize> = vec![];
    for (i, pair) in pairs.iter().enumerate() {
        let pair_index = i + 1;
        println!("Checking pair {}", pair_index);
        let right: bool = compare_pair(&pair);
        println!("  right order: {}", right);
        if right {
            right_order.push(pair_index);
        }
    }
    right_order.iter().sum()
}

fn compare_pair(pair: &Pair) -> bool {
    let mut right_order: bool = true;
    let left = &pair[0];
    let right = &pair[1];
    let mut left_keys: Vec<&Vec<u32>> = left.keys().collect();
    left_keys.sort();
    let mut right_keys: Vec<&Vec<u32>> = right.keys().collect();
    right_keys.sort();
    let mut left_iter = left_keys.iter();
    let mut right_iter = right_keys.iter();
    let mut is_same: bool = true;
    let mut get_next_left: bool = false;
    let mut get_next_right: bool = false;
    let mut compare_keys: bool = true;
    let mut l_option = left_iter.next();
    let mut r_option = right_iter.next();
    while is_same {
        if get_next_left {
            l_option = left_iter.next();
        }
        if get_next_right {
            r_option = right_iter.next();
        }
        get_next_left = true;
        get_next_right = true;
        if l_option == None {
            println!("  no left");
            break;
        }
        if r_option == None {
            println!("  no right");
            right_order = false;
            break;
        }
        let l = l_option.unwrap();
        let r = r_option.unwrap();
        if compare_keys {
            if l.len() != r.len() {
                right_order = l.len() < r.len();
                println!("  different length, left shorter: {}", right_order);
                break;
            }
        }
        compare_keys = true;
        let l_value = left.get(&l.to_vec()).unwrap();
        let r_value = right.get(&r.to_vec()).unwrap();
        println!("  comparing l {:?} ({}) - r {:?} ({})", l, l_value, r ,r_value);
        if l_value == &LIST || r_value == &LIST {
            if l_value == &LIST && r_value == &EMPTY_LIST {
                println!("  empty right");
                right_order = false;
                break;
            } else if l_value == &LIST && r_value >= &0 {
                get_next_left = true;
                get_next_right = false;
                compare_keys = false;
                continue;
            } else if l_value >= &0 && r_value == &LIST {
                get_next_left = false;
                get_next_right = true;
                compare_keys = false;
                continue;
            }
        }
        right_order = r_value >= l_value;
        is_same = l_value == r_value;
    }
    right_order
}
