document.addEventListener("DOMContentLoaded", function () {
    const menu = document.getElementById("menu");
    const burger = document.querySelector(".burger");
    const closeBtn = document.getElementById("close-menu");

    // Toggle menu via burger
    burger.addEventListener("click", function () {
        menu.classList.toggle("show");
    });

    // Close menu w/ arrow
    closeBtn.addEventListener("click", function () {
        menu.classList.remove("show");
    });

    // Dark/light mode
    const modeToggle = document.querySelector(".mode-toggle");

    modeToggle.addEventListener("click", function () {
        document.body.classList.toggle("light-mode");
        document.body.classList.toggle("dark-mode");
    });
});

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".custom-table tbody tr").forEach(row => {
        let status = row.getAttribute("data-status");

        if (status === "excellent") {
            row.style.backgroundColor = "#c8e6c9"; // Light green
        } else if (status === "average") {
            row.style.backgroundColor = "#fff9c4"; // Light yellow
        } else if (status === "poor") {
            row.style.backgroundColor = "#ffccbc"; // Light red
        }
    });
});

document.addEventListener("DOMContentLoaded", function() {
    // Get modal elements
    const modal = document.getElementById("student-management-modal");
    const manageUsersBtn = document.querySelector(".manage-users");
    const closeBtn = document.querySelector(".close-modal");
    const searchBtn = document.getElementById("search-btn");
    const studentSearch = document.getElementById("student-search");
    const addEnrollmentBtn = document.getElementById("add-enrollment-btn");
    
    // Role information
    const userRole = document.querySelector('.bigger-p:nth-child(3)').textContent.includes('TEACHER') 
      ? 'TEACHER' 
      : document.querySelector('.bigger-p:nth-child(3)').textContent.includes('ADMIN') 
        ? 'ADMIN' 
        : 'STUDENT';
    
    // Open modal when manage users button is clicked
    if (manageUsersBtn) {
      manageUsersBtn.addEventListener("click", function() {
        modal.style.display = "block";
        loadStudents();
        loadAvailableStudents();
        if (userRole === 'TEACHER') {
          loadTeacherSubjects();
        }
      });
    }
    
    // Close modal when X is clicked
    if (closeBtn) {
      closeBtn.addEventListener("click", function() {
        modal.style.display = "none";
      });
    }
    
    // Close modal when clicking outside the modal
    window.addEventListener("click", function(event) {
      if (event.target === modal) {
        modal.style.display = "none";
      }
    });
    
    // Search functionality
    if (searchBtn) {
      searchBtn.addEventListener("click", function() {
        const searchTerm = studentSearch.value.toLowerCase();
        filterStudents(searchTerm);
      });
    }
    
    if (studentSearch) {
      studentSearch.addEventListener("keyup", function(event) {
        if (event.key === "Enter") {
          const searchTerm = studentSearch.value.toLowerCase();
          filterStudents(searchTerm);
        }
      });
    }
    
    // Add enrollment functionality
    if (addEnrollmentBtn) {
      addEnrollmentBtn.addEventListener("click", function() {
        const studentSelect = document.getElementById("student-select");
        const subjectSelect = document.getElementById("subject-select");
        
        const studentId = studentSelect.value;
        const subjectId = subjectSelect.value;
        
        if (!studentId) {
          alert("Please select a student");
          return;
        }
        
        if (!subjectId) {
          alert("Please select a subject");
          return;
        }
        
        addStudentToSubject(studentId, subjectId);
      });
    }
    
    // Function to load teacher's subjects
    function loadTeacherSubjects() {
      fetch('/api/subjects/teacher')
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(subjects => {
          // Store the subjects for later use in removal dropdown
          window.teacherSubjects = subjects;
        })
        .catch(error => {
          console.error('Error fetching teacher subjects:', error);
        });
    }
    
    // Function to load students
    function loadStudents() {
      const studentList = document.getElementById("student-list");
      studentList.innerHTML = '<tr><td colspan="3">Loading students...</td></tr>';
      
      // Adjust endpoint based on role
      const endpoint = userRole === 'TEACHER' 
        ? '/api/enrollments/teacher-students' 
        : '/api/students-alternative';
      
      fetch(endpoint)
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(students => {
          if (students.length === 0) {
            studentList.innerHTML = '<tr><td colspan="3">No students found</td></tr>';
            return;
          }
          
          studentList.innerHTML = '';
          students.forEach(student => {
            const row = document.createElement('tr');
            
            // Different HTML based on role
            if (userRole === 'TEACHER') {
              row.innerHTML = `
                <td>${student.firstName} ${student.lastName}</td>
                <td>${student.email}</td>
                <td>
                  <select class="subject-removal-select" data-student-id="${student.id}">
                    <option value="">Select subject to remove from</option>
                  </select>
                  <button class="remove-student-btn" data-student-id="${student.id}">Remove</button>
                </td>
              `;
              
              // Add the student row first
              studentList.appendChild(row);
              
              // Then populate the subject dropdown for this student
              const subjectSelect = row.querySelector('.subject-removal-select');
              populateSubjectDropdownForStudent(subjectSelect, student.id);
            } else {
              // Admin view - simpler with just the remove button
              row.innerHTML = `
                <td>${student.firstName} ${student.lastName}</td>
                <td>${student.email}</td>
                <td>
                  <button class="remove-student-btn" data-student-id="${student.id}">Remove</button>
                </td>
              `;
              studentList.appendChild(row);
            }
          });
          
          // Add event listeners to remove buttons
          document.querySelectorAll('.remove-student-btn').forEach(button => {
            button.addEventListener('click', function() {
              const studentId = this.getAttribute('data-student-id');
              
              if (userRole === 'TEACHER') {
                const row = this.closest('tr');
                const subjectSelect = row.querySelector('.subject-removal-select');
                const subjectId = subjectSelect.value;
                
                if (!subjectId) {
                  alert('Please select a subject first');
                  return;
                }
                
                removeStudentFromSubject(studentId, subjectId);
              } else {
                // For admin, confirm removal from all subjects
                if (confirm('Are you sure you want to remove this student from all subjects?')) {
                  removeStudentFromAllSubjects(studentId);
                }
              }
            });
          });
          
          // Add event listeners to subject dropdown changes (for teachers)
          if (userRole === 'TEACHER') {
            document.querySelectorAll('.subject-removal-select').forEach(select => {
              select.addEventListener('change', function() {
                // Enable/disable the remove button based on selection
                const removeBtn = this.nextElementSibling;
                removeBtn.disabled = !this.value;
              });
            });
          }
        })
        .catch(error => {
          console.error('Error fetching students:', error);
          studentList.innerHTML = '<tr><td colspan="3">Error loading students</td></tr>';
        });
    }
    
    // Function to populate subject dropdown for a specific student
    function populateSubjectDropdownForStudent(selectElement, studentId) {
      // Fetch enrollments for this student
      fetch(`/api/enrollments/student/${studentId}/subjects`)
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(subjects => {
          // Clear existing options except the first one
          while (selectElement.options.length > 1) {
            selectElement.remove(1);
          }
          
          // Only add subjects that the current teacher teaches
          const teacherSubjects = window.teacherSubjects || [];
          const teacherSubjectIds = teacherSubjects.map(s => s.id);
          
          subjects.forEach(subject => {
            // Only add subjects that this teacher teaches
            if (teacherSubjectIds.includes(subject.id)) {
              const option = document.createElement('option');
              option.value = subject.id;
              option.textContent = subject.name;
              selectElement.appendChild(option);
            }
          });
          
          // Disable the button initially if no subjects
          const removeBtn = selectElement.nextElementSibling;
          removeBtn.disabled = selectElement.options.length <= 1;
        })
        .catch(error => {
          console.error('Error fetching student subjects:', error);
          selectElement.innerHTML = '<option value="">Error loading subjects</option>';
        });
    }
    
    // Function to load available students for enrollment
    function loadAvailableStudents() {
      const studentSelect = document.getElementById("student-select");
      
      fetch('/api/available-students')
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(students => {
          console.log('Available students response:', students);
          studentSelect.innerHTML = '<option value="">Select Student</option>';
          
          students.forEach(student => {
            const option = document.createElement('option');
            option.value = student.id;
            
            // Fix property names to match what's coming from the server
            // Java property naming convention when converted to JSON uses camelCase
            option.textContent = `${student.firstName} ${student.lastName} (${student.email})`;
            studentSelect.appendChild(option);
          });
        })
        .catch(error => {
          console.error('Error fetching available students:', error);
          studentSelect.innerHTML = '<option value="">Error loading students</option>';
        });
    }
    
    // Function to filter students
    function filterStudents(searchTerm) {
      const rows = document.querySelectorAll('#student-list tr');
      
      rows.forEach(row => {
        const name = row.cells[0].textContent.toLowerCase();
        const email = row.cells[1].textContent.toLowerCase();
        
        if (name.includes(searchTerm) || email.includes(searchTerm)) {
          row.style.display = '';
        } else {
          row.style.display = 'none';
        }
      });
    }
    
    // Function to add student to subject
    function addStudentToSubject(studentId, subjectId) {
      fetch('/api/enrollments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          studentId: studentId,
          subjectId: subjectId
        })
      })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to add student to subject');
        }
        
        alert('Student added to subject successfully');
        loadStudents();
        loadAvailableStudents();
      })
      .catch(error => {
        console.error('Error adding student to subject:', error);
        alert('Error adding student to subject');
      });
    }
    
// Function to remove student from specific subject (for teachers)
function removeStudentFromSubject(studentId, subjectId) {
    if (!confirm('Are you sure you want to remove this student from the selected subject?')) {
      return;
    }
    
    fetch(`/api/enrollments/student/${studentId}/subject/${subjectId}`, {
      method: 'DELETE'
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          throw new Error(text || 'Failed to remove student from subject');
        });
      }
      
      alert('Student removed from subject successfully');
      loadStudents();
    })
    .catch(error => {
      console.error('Error removing student from subject:', error);
      alert('Error: ' + error.message);
    });
  }
  
  // Function to remove student from all subjects (for admins)
  function removeStudentFromAllSubjects(studentId) {
    fetch(`/api/enrollments/student/${studentId}/all`, {
      method: 'DELETE'
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          throw new Error(text || 'Failed to remove student from all subjects');
        });
      }
      
      alert('Student removed from all subjects successfully');
      loadStudents();
    })
    .catch(error => {
      console.error('Error removing student from all subjects:', error);
      alert('Error: ' + error.message);
    });
  }
  });