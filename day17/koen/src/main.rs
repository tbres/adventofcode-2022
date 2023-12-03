use std::fs;
use std::str::Chars;
use std::iter::Cycle;
use std::vec::IntoIter;

type Coord = [i32; 2];

fn main() {
    // let path = String::from("data/input.txt");
    let path = String::from("data/input_test.txt");
    let text = fs::read_to_string(path).expect("Error reading the file");
    
    let jets = text.chars().cycle();
    let default_shapes = get_default_shapes();
    let shapes = default_shapes.into_iter().cycle();
   
    let floor = vec![
        [0, 0], [1, 0], [2, 0], [3, 0], [4, 0], [5, 0], [6, 0], [7, 0]
    ];
    let mut chamber = Chamber{
        left_wall: 0,
        right_wall: 8,
        jets: jets,
        shapes: shapes,
        vertices: floor
    };
    let njets = text.len();
    let ncycles = 1000000000000 / njets;
    let remaining = 1000000000000 % njets;
    println!("cycle: {}, ncycles: {}, remaning: {}", njets, ncycles, remaining);
    chamber.drop_rocks(10);
    // chamber.print();
    println!("{}", chamber.get_max_height());
}

#[derive(Debug)]
struct Shape {
    vertices: Vec<Coord>
}

impl Shape {

    fn set_origin(&mut self, origin: Coord) {
        let x_offset = origin[0] + 2;
        let y_offset = origin[1] + 3;
        for x in self.vertices.iter_mut() {
            x[0] += x_offset;
            x[1] += y_offset;
        }
    }

    fn set_shape(&mut self, vertices: Vec<Coord>) {
        self.vertices = vertices;
    }

    fn down(&self) -> Vec<Coord> {
        self.vertices.iter().map(|x| [x[0], x[1]-1]).collect()
    }

    fn left(&self) -> Vec<Coord> {
        self.vertices.iter().map(|x| [x[0]-1, x[1]]).collect()
    }

    fn right(&self) -> Vec<Coord> {
        self.vertices.iter().map(|x| [x[0]+1, x[1]]).collect()
    }

    fn do_move(&self, jet: char) -> Vec<Coord> {
        match jet {
            '<' => self.left(),
            '>' => self.right(),
            _ => self.vertices.to_vec()
        }
    }

}

struct Chamber<'a> {
    left_wall: i32,
    right_wall: i32,
    jets:Cycle<Chars<'a>>,
    shapes: Cycle<IntoIter<Vec<Coord>>>,
    vertices: Vec<Coord>
}

impl Chamber<'_> {

    fn get_max_height(&self) -> i32 {
        self.vertices.iter().map(|x| x[1]).max().unwrap()
    }

    fn check_walls(&self, vertices: &Vec<Coord>) -> bool {
        let left = vertices.iter().all(|&x| x[0] > self.left_wall);
        let right = vertices.iter().all(|&x| x[0] < self.right_wall);
        left && right
    }

    fn check_rocks(&self, vertices: &Vec<Coord>) -> bool {
        vertices.iter().any(|&x| self.vertices.contains(&x))
    }

    fn drop_rock(&mut self) {
        let height = self.get_max_height();
        let vertices = self.shapes.next().unwrap();
        let mut shape = Shape{vertices: vertices.to_vec()};
        shape.set_origin([0, height]);
        // self.print_move(&shape.vertices);
        loop {
            let jet = self.jets.next().unwrap();
            let new_vertices = shape.do_move(jet);
            if self.check_walls(&new_vertices) && !self.check_rocks(&new_vertices) {
                shape.set_shape(new_vertices);
            }
            // println!("move {}", jet);
            // self.print_move(&shape.vertices);
            let new_vertices = shape.down();
            if self.check_rocks(&new_vertices) {
                self.vertices.append(&mut shape.vertices);
                break;
            }
            shape.set_shape(new_vertices);
            // println!("move down");
            // self.print_move(&shape.vertices);
        }
    }

    fn drop_rocks(&mut self, n: usize) {
        for _i in 0..n {
            self.drop_rock();
        }
    }

    fn print_move(&self, vertices: &Vec<Coord>) {
        let height = self.get_max_height() + 4;
        for i in (1..height+1).rev() {
            let mut line = String::from("");
            for j in self.left_wall + 1..self.right_wall {
                let mut c = match self.vertices.contains(&[j, i]) {
                    true => '#',
                    false => '.'
                };
                if c == '.' {
                    c = match vertices.contains(&[j, i]) {
                        true => '@',
                        false => '.'
                    };
                }
                line.push(c);
            }
            println!("|{}|", line);
        }
        println!("+-------+");
        println!();
    }

    fn print(&self) {
        let height = self.get_max_height();
        for i in (1..height+1).rev() {
            let mut line = String::from("");
            for j in self.left_wall + 1..self.right_wall {
                let c = match self.vertices.contains(&[j, i]) {
                    true => '#',
                    false => '.'
                };
                line.push(c);
            }
            println!("|{}|", line);
        }
        println!("+-------+");
    }

}

fn get_default_shapes() -> Vec<Vec<Coord>> {
    vec![
        vec![[1, 1], [2, 1], [3, 1], [4, 1]], // Line
        vec![[2, 1], [1, 2], [2, 2], [3, 2], [2, 3]], // Cross
        vec![[1, 1], [2, 1], [3, 1], [3, 2], [3, 3]], // L
        vec![[1, 1], [1, 2], [1, 3], [1, 4]], // Vert Line
        vec![[1, 1], [2, 1], [1, 2], [2, 2]] // Box
    ]
}
