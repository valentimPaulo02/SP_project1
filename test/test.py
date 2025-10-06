import pandas as pd
import matplotlib.pyplot as plt
from sqlalchemy import create_engine

# --- DB connection ---
engine = create_engine("postgresql://postgres:password@localhost:5432/pds_proj_1")

# --- Load data ---
query = "SELECT id, age, gender, postal_code FROM med_data_deid"
df = pd.read_sql(query, engine)

print("✅ Data loaded:", df.shape[0], "rows")

# --- 1. Age distribution ---
def plot_age_distributions(df):
    plt.figure(figsize=(8,5))
    df['age'].dropna().hist(bins=30, edgecolor='black')
    plt.title("Age Distribution (raw)")
    plt.xlabel("Age")
    plt.ylabel("Count")
    plt.tight_layout()
    plt.savefig("age_distribution_raw.png")
    plt.close()

    # Try 5y, 10y, 20y bins
    for bin_size in [5, 10, 20]:
        labels = [f"{i}-{i+bin_size-1}" for i in range(0, 100, bin_size)]
        df[f'age_{bin_size}y'] = pd.cut(df['age'], bins=range(0, 101, bin_size), labels=labels, right=False)
        age_counts = df[f'age_{bin_size}y'].value_counts().sort_index()
        plt.figure(figsize=(10,5))
        age_counts.plot(kind='bar')
        plt.title(f"Age Distribution ({bin_size}-year bins)")
        plt.xlabel("Age group")
        plt.ylabel("Count")
        plt.xticks(rotation=45)
        plt.tight_layout()
        plt.savefig(f"age_distribution_{bin_size}y.png")
        plt.close()

#plot_age_distributions(df)

# --- 2. Gender distribution ---
def plot_gender_distribution(df):
    gender_counts = df['gender'].value_counts(dropna=False).sort_values(ascending=False)
    print("\nGender counts:\n", gender_counts)

    plt.figure(figsize=(8,5))
    gender_counts.plot(kind='bar')
    plt.title("Gender Distribution")
    plt.ylabel("Count")
    plt.tight_layout()
    plt.savefig("gender_distribution.png")
    plt.close()

#plot_gender_distribution(df)

# --- 3. Postal code distribution ---
def plot_postal_distribution(df):
    # Full postal codes
    postal_counts = df['postal_code'].value_counts()
    print("\nTop 10 most frequent full postal codes:\n", postal_counts.head(10))

    # First 3 digits generalization
    df['postal_3'] = df['postal_code'].astype(str).str[:3]
    postal3_counts = df['postal_3'].value_counts()

    plt.figure(figsize=(10,5))
    postal3_counts.head(20).plot(kind='bar')
    plt.title("Top 20 Postal Code Prefixes (3 digits)")
    plt.ylabel("Count")
    plt.xlabel("Prefix (first 3 digits)")
    plt.tight_layout()
    plt.savefig("postal_distribution_prefix3.png")
    plt.close()

#plot_postal_distribution(df)

# --- 4. Cross-tabulation: Age group × Gender ---
def cross_tab_age_gender(df):
    df['age_10y'] = pd.cut(df['age'], bins=range(0, 101, 10), labels=[f"{i}-{i+9}" for i in range(0, 100, 10)], right=False)
    crosstab = pd.crosstab(df['age_10y'], df['gender'])
    print("\nAge × Gender Crosstab:\n", crosstab)

    crosstab.plot(kind='bar', stacked=True, figsize=(12,6))
    plt.title("Age × Gender Distribution (10-year bins)")
    plt.xlabel("Age group")
    plt.ylabel("Count")
    plt.tight_layout()
    plt.savefig("age_gender_crosstab.png")
    plt.close()

#cross_tab_age_gender(df)

# --- Postal code length profiling ---
def postal_code_length_profile(df):
    # Ensure it's a string
    df['postal_code_str'] = df['postal_code'].astype(str)
    df['postal_len'] = df['postal_code_str'].str.len()

    length_counts = df['postal_len'].value_counts().sort_index()

    print("\nPostal code length distribution:")
    print(length_counts)

    # Save to CSV for reporting
    length_counts.to_csv("postal_code_length_distribution.csv", header=["count"])

    plt.figure(figsize=(8,5))
    length_counts.plot(kind='bar')
    plt.title("Postal Code Length Distribution")
    plt.xlabel("Length of postal code")
    plt.ylabel("Number of users")
    plt.tight_layout()
    plt.savefig("postal_code_length_distribution.png")
    plt.close()

    return length_counts

#postal_code_length_profile(df)

# --- Postal code normalization & prefix distribution analysis ---
def postal_prefix_distributions(df, prefix_lengths=[3, 4, 5]):
    import pandas as pd
    import matplotlib.pyplot as plt

    # Normalize all postal codes to 9 characters by right-padding with 'X'
    df['postal_code_str'] = df['postal_code'].astype(str).str.ljust(9, 'X')

    for p in prefix_lengths:
        prefix_col = f'prefix_{p}'
        df[prefix_col] = df['postal_code_str'].str[:p]

        group_sizes = df.groupby(prefix_col).size().sort_values(ascending=False)

        print(f"\n=== Prefix length {p} ===")
        print(group_sizes.head(10))  # top 10 most frequent groups
        print(f"Number of groups: {len(group_sizes)}")
        print(f"Group size stats:\n{group_sizes.describe()}")

        # Save to CSV for reporting
        group_sizes.to_csv(f"postal_prefix_{p}_distribution.csv", header=["count"])

        # Plot histogram of group sizes
        plt.figure(figsize=(8,5))
        group_sizes.hist(bins=30)
        plt.title(f"Distribution of group sizes (prefix {p} digits)")
        plt.xlabel("Number of users in group")
        plt.ylabel("Frequency of groups")
        plt.tight_layout()
        plt.savefig(f"postal_prefix_{p}_group_sizes.png")
        plt.close()

        # Plot top 20 groups
        plt.figure(figsize=(10,6))
        group_sizes.head(20).plot(kind='bar')
        plt.title(f"Top 20 postal groups by size (prefix {p} digits)")
        plt.xlabel("Prefix")
        plt.ylabel("Number of users")
        plt.tight_layout()
        plt.savefig(f"postal_prefix_{p}_top20.png")
        plt.close()

#postal_prefix_distributions(df, prefix_lengths=[3, 4, 5])


# --- 6. K-anonymity check on quasi-identifiers ---
def check_k_anonymity(df, k_threshold=5):
    """
    Computes k-anonymity based on quasi-identifiers:
    age_10y, gender, postal_3
    """
    # Ensure age_10y exists
    if 'age_10y' not in df.columns:
        df['age_10y'] = pd.cut(df['age'], bins=range(0, 101, 10),
                               labels=[f"{i}-{i+9}" for i in range(0, 100, 10)],
                               right=False)

    # Ensure postal_3 exists
    if 'postal_3' not in df.columns:
        df['postal_3'] = df['postal_code'].astype(str).str[:3]

    # Create combination key
    qi_columns = ['age_10y', 'gender', 'postal_3']
    df['qi_combination'] = df[qi_columns].astype(str).agg('-'.join, axis=1)

    # Count occurrences of each combination
    qi_counts = df['qi_combination'].value_counts()
    df['k'] = df['qi_combination'].map(qi_counts)

    # Summary statistics
    print("\n--- Quasi-Identifier Group Size Summary ---")
    print(qi_counts.describe())

    # Identify risky rows
    risky = df[df['k'] < k_threshold]
    print(f"\nNumber of rows with k < {k_threshold}: {risky.shape[0]}")
    print("Sample risky combinations:\n", risky[qi_columns + ['k']].head(10))

    # Save results
    df.to_csv("qi_k_anonymity_check.csv", index=False)
    qi_counts.to_csv("qi_group_sizes.csv", header=["count"])

    print("\n✅ k-anonymity check complete. Results saved to qi_k_anonymity_check.csv and qi_group_sizes.csv")

# Run k-anonymity check
check_k_anonymity(df, k_threshold=5)


# --- 5. Save summary stats to CSV ---
summary = {
    "n_rows": [df.shape[0]],
    "n_unique_genders": [df['gender'].nunique()],
    "n_unique_postal_codes": [df['postal_code'].nunique()],
    "missing_age": [df['age'].isna().sum()],
    "missing_gender": [df['gender'].isna().sum()],
    "missing_postal": [df['postal_code'].isna().sum()],
}
summary_df = pd.DataFrame(summary)
summary_df.to_csv("dataset_summary.csv", index=False)

print("\n✅ EDA complete. Outputs:")
print(" - age_distribution_raw.png, age_distribution_5y.png, age_distribution_10y.png, age_distribution_20y.png")
print(" - gender_distribution.png")
print(" - postal_distribution_prefix3.png")
print(" - age_gender_crosstab.png")
print(" - dataset_summary.csv")
