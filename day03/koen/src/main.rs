use std::vec::Vec;
use std::collections::HashMap;
use rustig::read_lines;

fn main() {

    // let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");
    let all_chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    let mut char_map = HashMap::new();
    for (i, c) in all_chars.chars().enumerate() {
        let c_prio: i32 = (i + 1).try_into().unwrap();
        char_map.insert(c, c_prio);
    }
    let mut all_prio: Vec<i32> = vec![];
    let mut group = vec![];
    let mut all_group_prio: Vec<i32> = vec![];
    for line in read_lines(path).unwrap() {
        let line = line.unwrap();
        // part 1
        let slice = line.len() / 2;
        let first = &line[..slice];
        let second = &line[slice..];
        let mut common = vec![];
        for c in first.chars() {
            if second.contains(c) && !common.contains(&c) {
                common.push(c);
                let prio: i32 = *char_map.get(&c).unwrap();
                all_prio.push(prio);
                // println!("{}", prio);
            }
        }
        // println!("{:?}", common);
        // part 2
        group.push(line);
        if group.len() == 3 {
            for c in group[0].chars() {
                if group[1].contains(c) && group[2].contains(c) {
                    println!("group badge: {}", c);
                    let group_prio: i32 = *char_map.get(&c).unwrap();
                    println!("group prio: {}", group_prio);
                    all_group_prio.push(group_prio);
                    group.clear();
                    break;
                }
            }
        }
    }

    // println!("individual prio: {:?}", all_prio);
    let prio_sum: i32 = all_prio.iter().sum::<i32>();
    println!("total individual prio:  {}", prio_sum);
    let group_prio_sum: i32 = all_group_prio.iter().sum::<i32>();
    println!("total group prio:  {}", group_prio_sum);
}
