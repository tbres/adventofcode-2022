use std::fs;
use std::cmp;
use std::collections::HashMap;

type Coord = Vec<u32>;
type Line = Vec<Coord>;
type Grid = HashMap<Coord, u32>;

const ROCK: u32 = 1;
const SAND: u32 = 2;

fn main() {
    // let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");
    let lines = parse_input(&path);
    // println!("{:? }", lines);
    let mut grid = draw_rocks(&lines);
    // println!("{:?}", grid);
    let seed: Coord = vec![500, 0];
    let saturation: usize = fill_cave(&seed, &mut grid, true);
    println!("Saturated with {} sand units", saturation);
}

fn parse_input(path: &String) -> Vec<Line> {
    let text = fs::read_to_string(path).expect("Error reading the file");
    let mut lines: Vec<Line> = vec![];
    for input_line in text.lines() {
        let mut line: Line = vec![];
        for coord_str in String::from(input_line).split(" -> ") {
            let coord: Coord = coord_str.split(",").map(|x| x.parse().unwrap()).collect();
            line.push(coord);
        }
        lines.push(line);
    }
    lines
}

fn draw_rocks(lines: &Vec<Line>) -> Grid {
    let mut grid: Grid = HashMap::new();
    for line in lines {
        for coord in get_line_vertices(line) {
            grid.insert(coord, ROCK);
        }
    }
    grid
}

fn get_line_vertices(line: &Line) -> Line {
    let mut expanded_line: Line = vec![];
    for i in 0 .. line.len() - 1 {
        let coord_a = &line[i];
        let coord_b = &line[i + 1];
        let x_min = cmp::min(coord_a[0], coord_b[0]);
        let x_max = cmp::max(coord_a[0], coord_b[0]);
        let y_min = cmp::min(coord_a[1], coord_b[1]);
        let y_max = cmp::max(coord_a[1], coord_b[1]);
        for x in x_min .. x_max + 1 {
            for y in y_min .. y_max + 1 {
                expanded_line.push(vec![x, y]);
            }
        }
    }
    expanded_line.sort();
    expanded_line.dedup();
    expanded_line
}

fn fill_cave(seed: &Coord, grid: &mut Grid, floor: bool) -> usize {
    let rocks= grid.len();
    let max_edge: u32 = grid.keys().map(|x| x[1]).max().unwrap();
    let mut saturation = grid.len();
    let mut filling: bool = true;
    while filling {
        saturation = grid.len();
        let sand_unit = seed.to_vec();
        let (stops, resting_unit) = drop_sand(&sand_unit, &grid, &max_edge, &floor);
        if stops {
            grid.insert(resting_unit, SAND);
        }
        filling = saturation != grid.len()
    }
    saturation - rocks
}

fn drop_sand(sand_unit: &Coord, grid: &Grid, max_edge: &u32, floor: &bool) -> (bool, Coord) {
    let mut stops: bool = true;
    let mut resting_unit: Coord = sand_unit.to_vec();
    let mut moving: bool = match grid.get(&resting_unit) {
        Some(_x) => false,
        None => true
    };
    while moving {
        let new_y = resting_unit[1] + 1;
        let below = vec![resting_unit[0], new_y];
        let left = vec![resting_unit[0] - 1, new_y];
        let right = vec![resting_unit[0] + 1, new_y];
        let mut below_material = grid.get(&below);
        let mut left_material = grid.get(&left);
        let mut right_material = grid.get(&right);
        if *floor && new_y >= *max_edge + 2{
            below_material = Some(&ROCK);
            left_material = Some(&ROCK);
            right_material = Some(&ROCK);
        }
        if below_material == None {
            resting_unit = below
        } else if left_material == None {
            resting_unit = left
        } else if right_material == None {
            resting_unit = right
        } else {
            moving = false;
        }
        if !*floor && resting_unit[1] > *max_edge {
            stops = false;
            break;
        }
    }
    (stops, resting_unit)
}