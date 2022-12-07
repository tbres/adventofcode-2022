use std::vec::Vec;
use std::collections::HashMap;
use std::path::PathBuf;

use lazy_static::lazy_static;
use regex::Regex;

use rustig::read_lines;

fn main() {
    //  let path = String::from(r"data\input_test.txt");
     let path = String::from(r"data\input.txt");

    let tree = parse_tree(&path);
    println!("{:?}", tree);
    
    let tree_size = resolve_tree_size(&tree);
    println!("{:?}", tree_size);

    let mut total_size: u32 = 0;
    for size in tree_size.values() {
        if size <= &100000 {
            total_size += size;
        }
    }
    println!("total size: {}", total_size);

    let system_size: u32 = 70000000;
    let required_size: u32 = 30000000;
    let root_size = tree_size.get(&"\\".to_string()).unwrap();
    let unused_size = system_size - root_size;
    let still_required_size = required_size - unused_size;
    println!("currently unused: {}", unused_size);
    println!("still required: {}", still_required_size);

    let mut options = vec![];
    for size in tree_size.values() {
        if size >= &still_required_size {
            options.push(size);
        }
    }
    let min_option = options.iter().min().unwrap();
    println!("deleted: {}", min_option);

}

#[derive(Debug)]
struct Dir {
    dirs: Vec<String>,
    files: Vec<u32>
}

fn parse_tree(input_path: &String) -> HashMap<String, Dir> {
    lazy_static! {
        static ref CD_RE: Regex = Regex::new(r"^\$ cd (.+)").unwrap();
    }
    lazy_static! {
        static ref DIR_RE: Regex = Regex::new(r"^dir (.+)").unwrap();
    }
    lazy_static! {
        static ref FILE_RE: Regex = Regex::new(r"^(\d+) .+").unwrap();
    }
    let mut tree: HashMap<String, Dir> = HashMap::new();
    let mut current_path = PathBuf::new();
    let mut dirs: Vec<String> = vec![];
    let mut files: Vec<u32> = vec![];
    for line in read_lines(input_path).unwrap() {
        let line = line.unwrap();
        if CD_RE.is_match(&line) {
            if !dirs.is_empty() || !files.is_empty() {
                tree.insert(
                    current_path.to_str().unwrap().to_string(),
                    Dir {
                        dirs: dirs.to_vec(),
                        files: files.to_vec()
                    }
                );
            }
            dirs.clear();
            files.clear();
            let caps = CD_RE.captures(&line).unwrap();
            let dir = caps.get(1).unwrap().as_str().replace("/", r"\");
            if dir == ".." {
                current_path = current_path.parent().unwrap().to_path_buf();
            } else {
                current_path = current_path.join(dir)
            }
            println!("current dir: {}", current_path.to_str().unwrap());           
        } else if DIR_RE.is_match(&line) {
            let caps = DIR_RE.captures(&line).unwrap();
            let dir = caps.get(1).unwrap().as_str().to_string();
            let full_dir = current_path.join(dir).to_str().unwrap().to_string();
            println!("  dir: {}", full_dir);
            dirs.push(full_dir);
        } else if FILE_RE.is_match(&line) {
            let caps = FILE_RE.captures(&line).unwrap();
            let file = caps.get(1).unwrap().as_str();
            println!("  file: {}", file);
            let file_size: u32 = file.parse().unwrap();
            files.push(file_size);
        }
    }
    if !dirs.is_empty() || !files.is_empty() {
        println!("  dirs: {:?}", dirs);
        println!("  files: {:?}", files);
        tree.insert(
            current_path.to_str().unwrap().to_string(),
            Dir {
                dirs: dirs.to_vec(),
                files: files.to_vec()
            }
        );
    }
    tree
}

fn resolve_tree_size(tree: &HashMap<String, Dir>) -> HashMap<&String, u32> {
    let mut tree_size = HashMap::new();
    for (path_string, dir) in tree.iter() {
        let dir_size:u32 = dir.files.iter().sum();
        tree_size.insert(path_string, dir_size);
    }
    let other_tree_size = tree_size.clone();
    for (dir, size) in tree_size.iter_mut() {
        for (other_dir, other_size) in other_tree_size.iter() {
            if dir != other_dir && other_dir.starts_with(&**dir) {
                *size += other_size;
            }
        }
    }
    tree_size
}
