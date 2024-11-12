#!/bin/bash
australia-southeast2-docker.pkg.dev/certification-pool/cloudbuild
# Check if input file is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <input_file>"
  exit 1
fi

# Source the input file to load variables (project ID, roles, user or service account emails)
source "$1"

# Check if required variables are set
if [ -z "$PROJECT_ID" ] || [ -z "$USER_EMAIL" ] || [ -z "$SERVICE_ACCOUNT_EMAIL" ] || [ -z "${USER_ROLES[*]}" ] || [ -z "${SERVICE_ACCOUNT_ROLES[*]}" ]; then
  echo "Error: PROJECT_ID, USER_EMAIL, SERVICE_ACCOUNT_EMAIL, or ROLES are not properly set in the input file."
  exit 1
fi

# Assign roles to the user
for ROLE in "${USER_ROLES[@]}"
do
  echo "Assigning $ROLE to user $USER_EMAIL in project $PROJECT_ID..."
  gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="user:$USER_EMAIL" \
    --role="$ROLE" \
    --condition="None"
done

# Assign roles to the service account
for ROLE in "${SERVICE_ACCOUNT_ROLES[@]}"
do
  echo "Assigning $ROLE to service account $SERVICE_ACCOUNT_EMAIL in project $PROJECT_ID..."
  gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$SERVICE_ACCOUNT_EMAIL" \
    --role="$ROLE" \
    --condition="None"
done

echo "All roles have been assigned!"
