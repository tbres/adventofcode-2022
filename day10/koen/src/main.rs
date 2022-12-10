use std::fs;
use std::vec::Vec;

fn main() {
    let path = String::from("data/input.txt");
    let text = fs::read_to_string(path).expect("Error reading the file");
    let commands: Vec<i32> = text.lines().map(|x| match x {
        "noop" => 0,
        &_ => String::from(x).drain(5..).collect::<String>().parse().unwrap()
    }).collect();
    let mut actions: Vec<i32> = vec![];
    for command in commands {
        if command != 0 {
            actions.push(0);
        }
        actions.push(command);
    }
    let mut cycles: Vec<i32> = vec![];
    let mut x: i32 = 1;
    let mut row: String = String::from("");
    cycles.push(x);
    for action in actions {
        cycles.push(x);
        let pixel_id = row.len() as i32;
        let pixel = if (x-1..x+2).contains(&pixel_id) { '#' } else { '.' };
        row.push(pixel);
        if row.len() == 40 {
            println!("{}", row);
            row.clear();
        }
        x += action;
    }
    let cycle_ids = [20, 60, 100, 140, 180, 220];
    let signal_strenghts = cycle_ids.map(|x| cycles[x] * x as i32);
    println!("{}", signal_strenghts.iter().sum::<i32>());
}
