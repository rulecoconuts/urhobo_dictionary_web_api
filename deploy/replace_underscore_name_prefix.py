import sys
import re

search_pattern = re.compile(r'(name_prefix|name)\s*=\s*\"(.+)\"')


def replace_underscore_in_line(line: str) -> str:
    matches = re.search(search_pattern, line)
    if not matches:
        return line
    start = matches.start(2)
    end = matches.end(2)

    new_line = line[:start] + line[start:end +
                                   1].replace("_", "-") + line[end+1:]
    return new_line


if __name__ == "__main__":
    file_name = sys.argv[1]
    old_file_lines = []
    with open(file_name, "r") as file:
        old_file_lines.extend(file.readlines())

    new_file_lines = [replace_underscore_in_line(
        line) for line in old_file_lines]

    with open(file_name, "w") as file:
        file.writelines(new_file_lines)
