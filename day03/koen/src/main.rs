use rustig::read_lines;

fn main() {

    let path = String::from(r"data\input.txt");

    for line in read_lines(path).unwrap() {
        let line = line.unwrap();
        println!("{}", line);
    }

}
