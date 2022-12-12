use std::fs;
use std::collections::HashMap;

type Coord = [i32; 2];
type TopoMap = HashMap<Coord, i32>;

fn main() {
    // let path = String::from(r"data\input_test.txt");
    let path = String::from(r"data\input.txt");
    let (start, end, size, map) = parse_input(&path);
    let visited: Vec<Coord> = vec![];
    let mut navigator = Navigator{start, end, size, map: &map, visited};
    let shortest_route = navigator.walk();
    println!("shortest route: {}", shortest_route);
    let mut routes: Vec<usize> = vec![];
    for (coord, height) in map.iter() {
        if height == &0 {
            let visited: Vec<Coord> = vec![];
            let mut navigator = Navigator{start: *coord, end, size, map: &map, visited};
            routes.push(navigator.walk());
        }
    }
    let min_route = routes.iter().min().unwrap();
    println!("Shortest of all routes: {}", min_route);
}

fn parse_input(path: &String) -> (Coord, Coord, Coord, TopoMap) {
    let text = fs::read_to_string(path).expect("Error reading the file");
    let mut start: Coord = [0; 2];
    let mut end: Coord = [0; 2];
    let mut coord: Coord = [0; 2];
    let mut map: TopoMap = HashMap::new();
    for (row, line) in text.lines().enumerate() {
        for (column, c) in line.chars().enumerate() {
            coord = [row as i32, column as i32];
            if c == 'S' {
                start = coord;
            } else if c == 'E' {
                end = coord;
            }
            let mut value = match c {
                'S' => u32::from('a'),
                'E' => u32::from('z'),
                _ => u32::from(c)
            };
            value -= u32::from('a');
            map.insert(coord, value as i32);
        }
    }
    let size = [coord[0] + 1, coord[1] + 1];
    (start, end, size, map)
}

#[derive(Clone)]
struct Route {
    coord: Coord,
    length: usize
}

impl Route {
    pub fn new(route: &Route, coord: Coord, ) -> Self {
        Route {
            coord: coord,
            length: route.length + 1
        }
    }
}

struct Navigator<'a> {
    start: Coord,
    end: Coord,
    size: Coord,
    map: &'a TopoMap,
    visited: Vec<Coord>
}

impl Navigator<'_> {

    fn walk(&mut self) -> usize {
        let mut shortest_route = self.size.iter().product::<i32>() as usize;
        let start_route = Route{coord: self.start, length: 0};
        let mut current_options = self.get_options(&start_route);
        while !current_options.is_empty() {
            let review_options = current_options.to_vec();
            current_options.clear();
            for route in review_options {
                if route.length < shortest_route {
                    let options = self.get_options(&route);
                    for option in options {
                        if option.length < shortest_route {
                            if option.coord == self.end {
                                shortest_route = option.length;
                            } else {
                                self.visited.push(option.coord);
                                current_options.push(option);
                            }
                        }
                    }
                }
            }
        }
        shortest_route
    }
    
    fn get_options(&self, route: &Route) -> Vec<Route> {
        let mut route_options: Vec<Route> = vec![];
        let last_height = self.get_height(&route.coord);
        let adjacent = get_adjacent(route.coord);
        let new_adjacent = adjacent.iter().filter(|x| !self.visited.contains(x));
        let options = new_adjacent.filter(|x| self.get_height(&x) <= last_height + 1);
        for option in options {
            let new_route = Route::new(route, *option);
            route_options.push(new_route);
        }
        route_options
    }

    fn get_height(&self, coord: &Coord) -> i32 {
        match self.map.get(coord) {
            Some(c) => *c,
            None => 99
        }
    }

}

fn get_adjacent(coord: Coord) -> Vec<Coord> {
    let mut adjacent: Vec<Coord> = vec![];
    for (i, j) in [(-1, 0), (1, 0), (0, -1), (0, 1)] {
        let node = [coord[0]+i, coord[1]+j];
        adjacent.push(node);
    }
    adjacent
}
