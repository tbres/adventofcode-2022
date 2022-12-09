use std::fs;
use std::iter;
use std::vec::Vec;

fn main() {

    // let path = String::from("data/input_test.txt");
    // let path = String::from("data/input_test2.txt");
    let path = String::from("data/input.txt");
    
    let moves = parse_moves(&path);

    let unique_visits = part1(&moves);
    println!("unique visits: {:?}", unique_visits);

    let unique_rope_visits = part2(&moves, 10);
    println!("unique visits: {:?}", unique_rope_visits);

}

fn parse_moves(path: &str) -> Vec<[i32; 2]> {
    let text = fs::read_to_string(path).expect("Error reading the file");
    let mut moves: Vec<[i32; 2]> = vec![];
    for line in text.lines() {
        // println!("{}", line);
        let n: i32 = String::from(line).split_off(2).parse().unwrap();
        let mut motion: Vec<[i32; 2]> = match &line[0..1] {
            "R" => iter::repeat([1, 0]).take(n.try_into().unwrap()).collect(),
            "L" => iter::repeat([-1, 0]).take(n.try_into().unwrap()).collect(),
            "U" => iter::repeat([0, 1]).take(n.try_into().unwrap()).collect(),
            "D" => iter::repeat([0, -1]).take(n.try_into().unwrap()).collect(),
            &_ => vec![[0, 0]]
        };
        // println!("  move {:?}", motion);
        moves.append(&mut motion);
    }
    moves
}

fn part1(moves: &Vec<[i32; 2]>) -> usize {
    let mut head: [i32; 2] = [0, 0];
    let mut tail: [i32; 2] = [0, 0];
    let mut visited: Vec<[i32; 2]> = vec![];
    visited.push(tail);
    for motion in moves {
        // println!("moving {:?}", motion);
        head = do_move(head, &motion);
        // println!("  new head {:?}", head);
        let touching = get_touching(tail);
        // println!("  touching {:?}", touching);
        if !touching.contains(&head) {
            tail = move_tail(tail, head);
            // println!("  new tail {:?}", tail);
            visited.push(tail);
        }
    }

    // println!("visited: {:?}", visited);
    visited.sort();
    visited.dedup();
    // println!("unique visited: {:?}", visited);
    visited.len()
}

fn part2(moves: &Vec<[i32; 2]>, size: usize) -> usize {
    let tail_idx = size - 1;
    let mut rope: Vec<[i32; 2]> = iter::repeat([0, 0]).take(size).collect();
    let mut visited: Vec<[i32; 2]> = vec![];
    visited.push(rope[tail_idx]);
    for motion in moves {
        rope[0] = do_move(rope[0], &motion);
        for i in 1..size {
            let touching = get_touching(rope[i]);
            if !touching.contains(&rope[i-1]) {
                rope[i] = move_tail(rope[i], rope[i-1]);
            }
        }
        visited.push(rope[tail_idx]);
    }
    visited.sort();
    visited.dedup();
    visited.len()
}

fn do_move(knot: [i32; 2], motion: &[i32; 2]) -> [i32; 2] {
    let result: Vec<i32> = knot.iter().zip(motion.iter()).map(|(a, b)| a + b).collect();
    result.try_into().unwrap()
}

fn get_touching(knot: [i32; 2]) -> Vec<[i32; 2]> {
    let mut touching: Vec<[i32; 2]> = vec![];
    for i in -1..2 {
        for j in -1..2 {
            let node = [knot[0]+i, knot[1]+j];
            touching.push(node);
        }
    }
    touching
}

fn move_tail(mut tail: [i32; 2], head: [i32; 2]) -> [i32; 2] {
    let x_shift = if head[0] > tail[0] { 1 } else { -1 };
    let y_shift = if head[1] > tail[1] { 1 } else { -1 };
    if tail[1] == head[1] {
        tail[0] += x_shift;
    } else if tail[0] == head[0] {
        tail[1] += y_shift;
    } else {
        tail[0] += x_shift;
        tail[1] += y_shift;
    }
    tail
}