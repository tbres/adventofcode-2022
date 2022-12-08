use std::vec::Vec;
use rustig::read_lines;


fn main() {
    //  let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");
    
    let grid = parse_input(&path);
    
    let total_visible = count_visible(&grid);
    println!("total visible: {}", total_visible);

    let scenic_score = calc_scenic_score(&grid);
    println!("best scenic score: {}", scenic_score);

}

fn parse_input(path: &String) -> Vec<Vec<u32>> {
    let mut grid:Vec<Vec<u32>> = vec![];
    for line in read_lines(path).unwrap() {
        let line = line.unwrap();
        let mut row = vec![];
        for c in line.chars() {
            let height: u32 = c.to_string().parse().unwrap();
            row.push(height);
        }
        grid.push(row);
    }
    grid
}

fn count_visible(grid: &Vec<Vec<u32>>) -> usize {
    let nrow = grid.len();
    let ncol = grid[0].len();
    println!("grid ({} x {}):", nrow, ncol);
    // println!();
    // for row in &grid {
    //     println!("  {:?}", row);
    // }
    // println!();

    let edges = (2 * nrow) + (2 * (ncol - 2 ));
    println!("{} visible on edges", edges);
    println!();

    let mut inner_visible: u32 = 0;
    for row in 1 .. nrow - 1 {
        for col in 1 .. ncol - 1 {
            let current_tree = grid[row][col];
            // println!("checking {} - {} : {}", row, col, current_tree);
            let left = &grid[row][0..col].iter().max().unwrap();
            let right = &grid[row][col+1..ncol].iter().max().unwrap();
            let up = &grid[0..row].iter().map(|s| s[col]).max().unwrap();
            let down = &grid[row+1..nrow].iter().map(|s| s[col]).max().unwrap();
            let any = [left, right, up, down];
            let any_min = any.iter().min().unwrap();
            if current_tree > **any_min {
                inner_visible += 1;
            }
        }
    }
    // println!();

    println!("{} inner visible", inner_visible);
    let total_visible = edges + usize::try_from(inner_visible).unwrap();
    total_visible
}

fn calc_scenic_score(grid: &Vec<Vec<u32>>) -> usize {
    let nrow = grid.len();
    let ncol = grid[0].len();
    let mut scores = vec![];
    for row in 1 .. nrow - 1 {
        for col in 1 .. ncol - 1 {
            let current_tree = grid[row][col];
            // println!("checking {} - {} : {}", row, col, current_tree);
            let mut left = grid[row][0..col].to_vec();
            let right = grid[row][col+1..ncol].to_vec();
            let mut up: Vec<u32> = grid[0..row].iter().map(|s| s[col]).collect();
            let down: Vec<u32> = grid[row+1..nrow].iter().map(|s| s[col]).collect();
            left.reverse();
            up.reverse();
            let left_score= left.iter().scan(true, |state, &x| check_tree(state, x, &current_tree)).collect::<Vec<u32>>().len();
            let right_score= right.iter().scan(true, |state, &x| check_tree(state, x, &current_tree)).collect::<Vec<u32>>().len();
            let up_score= up.iter().scan(true, |state, &x| check_tree(state, x, &current_tree)).collect::<Vec<u32>>().len();
            let down_score= down.iter().scan(true, |state, &x| check_tree(state, x, &current_tree)).collect::<Vec<u32>>().len();
            // println!("  left score: {}", left_score);
            // println!("  right score: {}", right_score);
            // println!("  up score: {}", up_score);
            // println!("  down score: {}", down_score);
            let score = left_score * right_score * up_score * down_score;
            scores.push(score);
        }
    }
    let max_score = scores.iter().max().unwrap();
    *max_score
}

fn check_tree(state: &mut bool, x: u32, current: &u32) -> Option<u32> {
    let result = match state {
        true => Some(x),
        false => None
    };
    *state = x < *current;
    result
}