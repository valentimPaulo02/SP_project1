import importlib
 

TASKS = [
    ("scripts.get_candidates", ["get_med_deid_uniques", "get_work_smallgroups"]),
    ("scripts.link_candidates", ["link_candidates"]),
    ("scripts.analysis", ["analysis"])
]

def run_task(script: str, functions: list):
    
    print(f"========= Running script: {script} =========")
    module = importlib.import_module(script)

    for func in functions:
        print(f"\n         -> Executing {func}() <-")
        func = getattr(module, func)
        func()
    print(f"\n========= Finished script: {script} =========")

def main():
    
    for script, funcs in TASKS:
        run_task(script, funcs)
        print("\n\nWaiting for next Script...\n\n")

    print("All scripts completed successfully.")
    print("All results can be checked in the ./ou_files folder.\n")

if __name__ == "__main__":
    main()