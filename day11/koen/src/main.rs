use std::fs;
use std::vec::Vec;
use std::collections::HashMap;
use lazy_static::lazy_static;
use regex::Regex;

lazy_static! {
    static ref ITEMS_RE: Regex = Regex::new(r"Starting items: (\d+(, \d+)*)").unwrap();
}
lazy_static! {
    static ref OPERATION_RE: Regex = Regex::new(r"Operation: new = old (?P<o>\*|\+) (?P<p>old|\d+)").unwrap();
}
lazy_static! {
    static ref DIVISOR_RE: Regex = Regex::new(r"Test: divisible by (\d+)").unwrap();
}    
lazy_static! {
    static ref TRUE_RE: Regex = Regex::new(r"If true: throw to monkey (\d+)").unwrap();
}
lazy_static! {
    static ref FALSE_RE: Regex = Regex::new(r"If false: throw to monkey (\d+)").unwrap();
}

fn main() {
    let path = String::from("data/input.txt");
    // let path = String::from("data/input_test.txt");
    let mut monkeys = parse_input(&path);
    // let monkey_business = do_rounds(&mut monkeys, 20, 3);
    let monkey_business = do_rounds(&mut monkeys, 10000, 1);
    println!("Monkey business: {}", monkey_business);
}

fn parse_input(path: &String) -> HashMap<u128, Monkey> {
    let text = fs::read_to_string(path).expect("Error reading the file");
    let mut monkeys:HashMap<u128, Monkey> = HashMap::new();
    let mut monkey_string: String = String::from("");
    let mut monkey_id: u128 = 0;
    for line in text.lines() {
        if !line.is_empty() {
            monkey_string.push_str(line);
        } else {
            monkeys.insert(monkey_id, parse_monkey(&monkey_string));
            monkey_id += 1;
            monkey_string.clear();
        }
    }
    if !monkey_string.is_empty() {
        monkeys.insert(monkey_id, parse_monkey(&monkey_string));
    }
    monkeys
}

fn parse_monkey(monkey_string: &String) ->  Monkey {
    let items_caps = ITEMS_RE.captures(&monkey_string).unwrap();
    let operation_caps = OPERATION_RE.captures(&monkey_string).unwrap();
    let divisor_caps = DIVISOR_RE.captures(&monkey_string).unwrap();
    let true_caps = TRUE_RE.captures(&monkey_string).unwrap();
    let false_caps = FALSE_RE.captures(&monkey_string).unwrap();
    let items_str = items_caps.get(1).unwrap().as_str();
    let items: Vec<u128> = items_str.split(", ").map(|x| x.parse().unwrap()).collect();
    let operator = operation_caps.name("o").unwrap().as_str();
    let operation = match operator {
        "*" => |a, b| a * b,
        "+" => |a, b| a + b,
        &_ => todo!()
    };
    let param_str =  operation_caps.name("p").unwrap().as_str();
    let param: u128 = match param_str {
        "old" => 0,
        _ => param_str.parse().unwrap()
    };
    let divisor: u128 = divisor_caps.get(1).unwrap().as_str().parse().unwrap();
    let to_true: u128 = true_caps.get(1).unwrap().as_str().parse().unwrap();
    let to_false: u128 = false_caps.get(1).unwrap().as_str().parse().unwrap();
    Monkey{
        items,
        operation,
        param,
        divisor,
        to: [to_true, to_false],
        inspected: 0
    }
}

fn do_rounds(monkeys: &mut HashMap<u128, Monkey>, n: u128, correction: u128) -> u128 {
    let mut params: Vec<u128> = monkeys.values().map(|x| x.param).filter(|x| x > &0).collect();
    let mut divisors: Vec<u128> = monkeys.values().map(|x| x.divisor).filter(|x| x > &0).collect();
    params.append(&mut divisors);
    params.sort();
    params.dedup();
    let modulo: u128 = params.iter().product();
    let m = monkeys.len() as u128;
    // println!("Initial state");
    // for (key, val) in monkeys.iter() {
    //     println!("  monkey {}: {:?}", key, val.items);
    // }
    for i in 1 .. n + 1 {
        if i % 5 == 0 {
            println!("Round {}", i);
        }
        for monkey_id in 0 .. m {
            let monkey = monkeys.get_mut(&monkey_id).expect("REASON");
            let items_to = monkey.inspect_items(&correction, &modulo);
            for item_to in items_to {
                monkeys.get_mut(&item_to[1]).expect("REASON").items.push(item_to[0]);
            }
        }
        // for (key, val) in monkeys.iter() {
        //     println!("  monkey {}: {:?}", key, val.items);
        // }
    }
    let mut monkey_inspected: Vec<u128> = monkeys.values().map(|x| x.inspected).collect();
    // println!("{:?}", monkey_inspected);
    monkey_inspected.sort();
    monkey_inspected.reverse();
    let busy_monkeys = &monkey_inspected[..2];
    busy_monkeys.iter().product()
}

struct Monkey {
    items: Vec<u128>,
    pub operation: fn(u128, u128) -> u128,
    param: u128,
    divisor: u128,
    to: [u128; 2],
    inspected: u128
}

impl Monkey {

    fn do_operation(&self, level: &u128) -> u128 {
        let a = if self.param == 0 { *level } else { self.param };
        (self.operation)(*level, a)
    }

    fn inspect_item(&self, level: &u128, correction: &u128, modulo: &u128) -> [u128; 2] {
        let mut new_level = self.do_operation(&level);
        new_level = new_level / correction;
        let to = match new_level % self.divisor {
            0 => self.to[0],
            _ => self.to[1]
        };
        new_level = new_level % modulo;
        [new_level, to]
    }
    fn inspect_items(&mut self, correction: &u128, modulo: &u128) -> Vec<[u128; 2]> {
        let mut items_to: Vec<[u128; 2]> = vec![];
        for item in &self.items {
            items_to.push(self.inspect_item(&item, correction, modulo));
            self.inspected += 1;
        }
        self.items.clear();
        items_to
    }

}