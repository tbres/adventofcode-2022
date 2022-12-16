use std::fs;
use std::cmp;
use lazy_static::lazy_static;
use regex::Regex;

type Coord = [i32; 2];

lazy_static! {
    static ref RE: Regex = Regex::new(r"Sensor at x=(?P<sx>-?\d+), y=(?P<sy>-?\d+): closest beacon is at x=(?P<bx>-?\d+), y=(?P<by>-?\d+)").unwrap();
}


fn main() {
    // let path = String::from("data/input_test.txt");
    let path = String::from("data/input.txt");
    let pairs = parse_input(&path);
    // println!{"{:?}", pairs};
    // let row: i32 = 10;
    let row: i32 = 2000000;
    let positions = count_positions(&pairs, &row);
    println!("Positions: {}", positions);
}

fn parse_input(path: &String) -> Vec<Pair> {
    let mut pairs: Vec<Pair> = vec![];
    let text = fs::read_to_string(path).expect("Error reading the file");
    for line in text.lines() {
        let caps = RE.captures(&line).unwrap();
        let sensor_x: i32 = caps.name("sx").unwrap().as_str().parse().unwrap();
        let sensor_y: i32 = caps.name("sy").unwrap().as_str().parse().unwrap();
        let beacon_x: i32 = caps.name("bx").unwrap().as_str().parse().unwrap();
        let beacon_y: i32 = caps.name("by").unwrap().as_str().parse().unwrap();
        let sensor: Coord = [sensor_x, sensor_y];
        let beacon: Coord = [beacon_x, beacon_y];
        let pair: Pair = Pair{sensor, beacon};
        pairs.push(pair);
    }
    pairs
}


fn count_positions(pairs: &Vec<Pair>, row: &i32) -> i32 {
    let mut stack: RangeStack = RangeStack{ranges: vec![]};
    for (i, pair) in pairs.iter().enumerate() {
        println!("Checking pair {}", i + 1);
        let dist = pair.manhattan_distance();
        let ymin = pair.sensor[1] - dist;
        let ymax = pair.sensor[1] + dist;
        let yrange = ymin .. ymax + 1;
        if yrange.contains(&row) {
            let ydist = (pair.sensor[1] - row).abs();
            let xdist = (dist - ydist).abs();
            let xmin = pair.sensor[0] - xdist;
            let xmax = pair.sensor[0] + xdist;
            println!("  adding range {} - {}", xmin, xmax);
            stack.add(&Range{min: xmin, max: xmax});
        }
    }
    let mut sensors: Vec<Coord> = pairs.iter().map(|x| x.sensor).filter(|x| x[1] == *row).collect();
    let mut beacons: Vec<Coord> = pairs.iter().map(|x| x.beacon).filter(|x| x[1] == *row).collect();
    sensors.append(&mut beacons);
    count_ranges(&stack, &sensors)
}

fn count_ranges(stack: &RangeStack, sensors: &Vec<Coord>) -> i32 {
    let mut total_count: i32 = 0;
    for range in &stack.ranges {
        println!("{:?}", range);
        let mut count: i32 = range.count();
        let r = range.min..range.min;
        let present: Vec<&Coord> = sensors.iter().filter(|x| r.contains(&x[1])).collect();
        count -= present.len() as i32;
        total_count += count;
    }
    total_count
}

#[derive(Debug)]
struct Pair {
    sensor: Coord,
    beacon: Coord
}

impl Pair {
    fn manhattan_distance(&self) -> i32 {
        manhattan_distance(&self.sensor[0], &self.sensor[1], &self.beacon[0], &self.beacon[1])
    }
}

fn manhattan_distance(x1: &i32, y1: &i32, x2: &i32, y2: &i32) -> i32 {
    let x_dist = x1 - x2;
    let y_dist = y1 - y2;
    x_dist.abs() + y_dist.abs()
}

#[derive(Debug, Copy, Clone)]
struct Range {
    min: i32,
    max: i32
}

impl Range {

    fn overlaps(&self, other: &Range) -> bool {
        let own_range = self.min .. self.max;
        own_range.contains(&other.min) || own_range.contains(&other.max)
    }

    fn merge(&mut self, other: &Range) {
        self.min = cmp::min(self.min, other.min);
        self.max = cmp::max(self.max, other.max);
    }

    fn count(&self) -> i32 {
        (self.max - self.min).abs()
    }

}

struct RangeStack {
    ranges: Vec<Range>
}

impl RangeStack {

    fn add(&mut self, other: &Range) {
        let mut merged: bool = false;
        for range in self.ranges.iter_mut() {
            if range.overlaps(other) {
                range.merge(other);
                merged = true;
            }
        }
        if !merged {
            self.ranges.push(*other);
        }
    }

}